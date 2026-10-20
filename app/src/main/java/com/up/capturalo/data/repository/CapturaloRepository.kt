package com.up.capturalo.data.repository

import com.up.capturalo.data.models.Materia
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de Capturalo.
 * Las capas superiores (ViewModel) sólo conocen esta interfaz,
 * nunca la implementación concreta.
 */
interface CapturaloRepository {

    // ---------- Materias ----------

    /** Flujo reactivo con la lista actualizada de materias. */
    fun obtenerMaterias(): Flow<List<Materia>>

    /** Crea y persiste una nueva materia; devuelve el objeto con su ID asignado. */
    suspend fun agregarMateria(nombre: String, profesor: String, colorHex: String): Materia

    /** Reemplaza los datos de una materia existente. */
    suspend fun actualizarMateria(materia: Materia)

    /** Elimina la materia con el [id] dado. */
    suspend fun eliminarMateria(id: Int)
}