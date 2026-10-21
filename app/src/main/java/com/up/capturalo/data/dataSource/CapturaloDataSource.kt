package com.up.capturalo.data.dataSource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.up.capturalo.data.models.Captura
import com.up.capturalo.data.models.Materia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.io.IOException

// -----------------------------------------------------------------------------
// CONFIGURACIÓN DE DATASTORE (Propiedad de Extensión de Kotlin)
//
// CONCEPTO EDUCATIVO - Propiedad de Extensión `Context.dataStore`:
// `by preferencesDataStore(...)` es un delegado que garantiza la creación de
// un ÚNICO archivo de preferencias (Singleton) por proceso. Al definirlo como
// extensión del Context a nivel de archivo (fuera de la clase), evitamos que
// se creen múltiples instancias de DataStore compitiendo por el mismo archivo físico
// en disco ("capturalo_preferences.preferences_pb").
// -----------------------------------------------------------------------------
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "capturalo_preferences")

/**
 * Fuente de datos persistente basada en Jetpack DataStore Preferences + Gson.
 *
 * @param context El [Context] de Android.
 *  - CONCEPTO EDUCATIVO: DataStore necesita acceder al almacenamiento interno
 *    privado de la aplicación (/data/user/0/com.up.capturalo/files/datastore/).
 *    En Android, solo a través de una instancia de [Context] podemos interactuar
 *    con el sistema de archivos del SO de forma segura.
 */
class CapturaloDataSource(
    private val context: Context
) {

    // -------------------------------------------------------------------------
    // CLAVE DE PREFERENCIA Y GSON
    //
    // DataStore Preferences almacena pares Clave-Valor.
    // Usamos `stringPreferencesKey` para indicar que el valor asociado a esta
    // clave será un String (en nuestro caso, el JSON generado por Gson).
    // -------------------------------------------------------------------------
    private val KEY_MATERIAS_JSON = stringPreferencesKey("key_materias_json")

    // Instancia de Gson para serializar (List -> JSON) y deserializar (JSON -> List).
    private val gson = Gson()

    // Semilla inicial (DataSeed) para inicializar los datos si la app se abre por primera vez.
    private val materiasSemilla = listOf(
        Materia(1, "Cálculo Multivariable", "Prof. Sirgei", "4CAF50"),
        Materia(2, "Cálculo Integral",      "Prof. Sirgei", "2196F3"),
        Materia(3, "Cálculo Vectorial",     "Prof. Sirgei", "9C27B0"),
    )

    // -------------------------------------------------------------------------
    // materiasFlow: Flujo Reactivo de Lectura
    //
    // CONCEPTOS EDUCATIVOS:
    // 1. `Flow<T>`: Es un flujo de datos asíncrono y frío de Kotlin Coroutines.
    //    Cada vez que se modifica el archivo de DataStore en disco, este Flow
    //    emite automáticamente la nueva lista transformada a quienes estén observando.
    // 2. `catch { ... }`: DataStore emite una [IOException] si falla la lectura de disco
    //    (archivo corrupto, error de E/S). Capturamos el error y emitimos preferencias
    //    vacías para evitar que la aplicación se caiga (crash).
    // 3. `TypeToken`: En tiempo de compilación Java/Kotlin borra la información de tipos genéricos
    //    ("Type Erasure"). Para que Gson sepa que queremos reconstruir un `List<Materia>`
    //    (y no un ArrayList genérico o un Map), `object : TypeToken<List<Materia>>() {}.type`
    //    captura el tipo exacto en tiempo de ejecución.
    // -------------------------------------------------------------------------
    val materiasFlow: Flow<List<Materia>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val json = preferences[KEY_MATERIAS_JSON]
            if (json.isNullOrBlank()) {
                // Si aún no hay nada guardado en DataStore, devolvemos la lista semilla.
                materiasSemilla
            } else {
                try {
                    val tipoLista = object : TypeToken<List<Materia>>() {}.type
                    gson.fromJson<List<Materia>>(json, tipoLista) ?: materiasSemilla
                } catch (e: Exception) {
                    // Si ocurre un error al parsear el JSON, degradamos de forma segura a la semilla.
                    materiasSemilla
                }
            }
        }

    /**
     * Expone el flujo reactivo de materias para las capas superiores (Repository).
     */
    fun obtenerMaterias(): Flow<List<Materia>> = materiasFlow

    // -------------------------------------------------------------------------
    // guardarMaterias: Escritura en disco
    //
    // CONCEPTOS EDUCATIVOS:
    // 1. `suspend`: Función de suspensión de Coroutines. Indica que puede pausar
    //    su ejecución mientras escribe en disco y reanudarse cuando termine,
    //    todo sin congelar el hilo principal de la interfaz gráfica (UI Thread).
    // 2. `dataStore.edit`: Transacción atómica. Modifica las preferencias de forma segura;
    //    si ocurre un fallo a medio proceso, la transacción se descarta sin corromper el archivo.
    // -------------------------------------------------------------------------
    suspend fun guardarMaterias(lista: List<Materia>) {
        val json = gson.toJson(lista)
        context.dataStore.edit { preferences ->
            preferences[KEY_MATERIAS_JSON] = json
        }
    }

    // -------------------------------------------------------------------------
    // OPERACIONES CRUD DE MATERIAS
    // -------------------------------------------------------------------------

    /**
     * Agrega una nueva materia calculando un nuevo ID autoincremental
     * y persiste el cambio inmediatamente en DataStore.
     */
    suspend fun agregarMateria(nombre: String, profesor: String, colorHex: String): Materia {
        // .first() toma el valor emitido más reciente del Flow en este instante.
        val listaActual = materiasFlow.first()
        val nuevoId = (listaActual.maxOfOrNull { it.id } ?: 0) + 1
        val nuevaMateria = Materia(
            id = nuevoId,
            nombre = nombre,
            profesor = profesor,
            color = colorHex
        )
        val nuevaLista = listaActual + nuevaMateria
        guardarMaterias(nuevaLista)
        return nuevaMateria
    }

    /**
     * Actualiza la materia cuyo ID coincida y guarda la lista modificada en disco.
     */
    suspend fun actualizarMateria(materia: Materia) {
        val listaActual = materiasFlow.first()
        val nuevaLista = listaActual.map { if (it.id == materia.id) materia else it }
        guardarMaterias(nuevaLista)
    }

    /**
     * Elimina la materia del ID proporcionado y actualiza la lista en disco.
     */
    suspend fun eliminarMateria(id: Int) {
        val listaActual = materiasFlow.first()
        val nuevaLista = listaActual.filter { it.id != id }
        guardarMaterias(nuevaLista)
    }

    // -------------------------------------------------------------------------
    // PERSISTENCIA DE CAPTURAS CON DATASTORE + GSON
    // -------------------------------------------------------------------------

    private val KEY_CAPTURAS_JSON = stringPreferencesKey("key_capturas_json")

    private val capturasSemilla = listOf(
        Captura(1, 1, "", System.currentTimeMillis()),
        Captura(2, 1, "", System.currentTimeMillis()),
        Captura(3, 2, "", System.currentTimeMillis()),
        Captura(4, 3, "", System.currentTimeMillis()),
    )

    /** Flujo reactivo de todas las capturas almacenadas en DataStore */
    val capturasFlow: Flow<List<Captura>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val json = preferences[KEY_CAPTURAS_JSON]
            if (json.isNullOrBlank()) {
                capturasSemilla
            } else {
                try {
                    val tipoLista = object : TypeToken<List<Captura>>() {}.type
                    gson.fromJson<List<Captura>>(json, tipoLista) ?: capturasSemilla
                } catch (e: Exception) {
                    capturasSemilla
                }
            }
        }

    /** Guarda la lista completa de capturas serializada en formato JSON en DataStore */
    suspend fun guardarCapturas(lista: List<Captura>) {
        val json = gson.toJson(lista)
        context.dataStore.edit { preferences ->
            preferences[KEY_CAPTURAS_JSON] = json
        }
    }

    /** Retorna el flujo con todas las capturas registradas */
    fun obtenerTodasLasCapturas(): Flow<List<Captura>> = capturasFlow

    /** Filtra reactivamente las capturas pertenecientes a una materia */
    fun obtenerCapturasPorMateria(idMateria: Int): Flow<List<Captura>> =
        capturasFlow.map { lista -> lista.filter { it.idMateria == idMateria } }

    /** Agrega una nueva captura asociada a [idMateria] con la ruta de archivo [rutaImagen] */
    suspend fun agregarCaptura(idMateria: Int, rutaImagen: String): Captura {
        val listaActual = capturasFlow.first()
        val nuevoId = (listaActual.maxOfOrNull { it.id } ?: 0) + 1
        val nueva = Captura(
            id = nuevoId,
            idMateria = idMateria,
            rutaImagen = rutaImagen,
            fecha = System.currentTimeMillis(),
        )
        val nuevaLista = listaActual + nueva
        guardarCapturas(nuevaLista)
        return nueva
    }

    /** Elimina la captura indicada por [id] de DataStore */
    suspend fun eliminarCaptura(id: Int) {
        val listaActual = capturasFlow.first()
        val nuevaLista = listaActual.filter { it.id != id }
        guardarCapturas(nuevaLista)
    }
}