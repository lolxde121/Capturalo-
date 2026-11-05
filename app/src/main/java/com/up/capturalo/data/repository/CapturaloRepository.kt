package com.up.capturalo.data.repository

import android.graphics.Bitmap
import com.up.capturalo.data.models.Captura
import com.up.capturalo.data.models.Materia
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del Repositorio según los principios de Clean Architecture.
 *
 * CONCEPTOS EDUCATIVOS Y SOLID:
 * 1. Desacoplamiento (Principio de Inversión de Dependencias - SOLID):
 *    Las capas superiores (UI y ViewModels) dependen de esta ABSTRACCIÓN (interfaz),
 *    no de la clase concreta `CapturaloRepositoryImp` ni de detalles de persistencia local.
 *    Toda la persistencia es 100% LOCAL (DataStore + Gson) sin conexiones de red.
 *
 * 2. Single Responsibility Principle (SRP):
 *    Unifica la fuente de datos (DataStore) y el almacenamiento físico de fotos (StorageService)
 *    ofreciendo una API limpia y coherente a la capa de presentación.
 *
 * 3. Flujo Reactivo (`Flow<T>`):
 *    Permite a los observadores reaccionar en tiempo real a las modificaciones de los datos.
 *
 * 4. Suspensión Asíncrona (`suspend`):
 *    Garantiza que cualquier escritura en disco se realice de forma segura dentro de una
 *    coroutine sin congelar la interfaz de usuario.
 */
interface CapturaloRepository {

    /**
     * Flujo reactivo con la lista actualizada de materias en tiempo real.
     */
    fun obtenerMaterias(): Flow<List<Materia>>

    /**
     * Agrega una nueva materia al medio persistente y retorna la instancia con su ID asignado.
     */
    suspend fun agregarMateria(nombre: String, profesor: String, colorHex: String): Materia

    /**
     * Actualiza los datos de una materia ya existente en el medio persistente.
     */
    suspend fun actualizarMateria(materia: Materia)

    /**
     * Elimina del medio persistente la materia con el [id] dado.
     */
    suspend fun eliminarMateria(id: Int)

    /**
     * Obtiene el flujo de capturas asociadas a una materia específica.
     */
    fun obtenerCapturasPorMateria(idMateria: Int): Flow<List<Captura>>

    /**
     * Obtiene el flujo con todas las capturas registradas en el sistema.
     */
    fun obtenerTodasLasCapturas(): Flow<List<Captura>>

    /**
     * Agrega una nueva captura fotográfica asociada a una materia con una ruta ya existente.
     */
    suspend fun agregarCaptura(idMateria: Int, rutaImagen: String): Captura

    /**
     * Comprime y almacena el [bitmap] en el disco local y posteriormente registra
     * la captura en Jetpack DataStore de forma atómica y coordinada.
     */
    suspend fun registrarCapturaConFoto(idMateria: Int, bitmap: Bitmap): Captura

    /**
     * Elimina la captura indicada por [id] de la persistencia de DataStore.
     */
    suspend fun eliminarCaptura(id: Int)

    /**
     * Elimina la captura de DataStore y su archivo de imagen físico correspondiente del disco.
     */
    suspend fun eliminarCapturaConArchivo(captura: Captura)
}
