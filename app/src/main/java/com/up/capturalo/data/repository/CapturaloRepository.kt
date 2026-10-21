package com.up.capturalo.data.repository

import com.up.capturalo.data.models.Captura
import com.up.capturalo.data.models.Materia
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del Repositorio según los principios de Clean Architecture.
 *
 * CONCEPTOS EDUCATIVOS:
 * 1. Desacoplamiento (Principio de Inversión de Dependencias - SOLID):
 *    Las capas superiores (UI y ViewModels) dependen de esta ABSTRACCIÓN (interfaz),
 *    no de la clase concreta `CapturaloRepositoryImp` ni de detalles de persistencia local.
 *    Toda la persistencia es 100% LOCAL (DataStore + Gson) sin conexiones de red.
 *
 * 2. `Flow<List<Materia>>`:
 *    Permite a los observadores reaccionar en tiempo real a las modificaciones de los datos.
 *
 * 3. `suspend`:
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
     * Agrega una nueva captura fotográfica asociada a una materia.
     */
    suspend fun agregarCaptura(idMateria: Int, rutaImagen: String): Captura

    /**
     * Elimina la captura indicada por [id].
     */
    suspend fun eliminarCaptura(id: Int)
}