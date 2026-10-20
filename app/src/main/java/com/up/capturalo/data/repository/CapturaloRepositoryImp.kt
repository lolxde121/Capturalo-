package com.up.capturalo.data.repository

import com.up.capturalo.data.dataSource.CapturaloDataSource
import com.up.capturalo.data.models.Materia
import kotlinx.coroutines.flow.Flow

/**
 * Implementación concreta de [CapturaloRepository].
 * Delega al [CapturaloDataSource] y adapta sus StateFlow a Flow genérico
 * para no exponer detalles de implementación hacia arriba.
 */
class CapturaloRepositoryImp(
    private val dataSource: CapturaloDataSource
) : CapturaloRepository {

    // ---------- Materias ----------

    override fun obtenerMaterias(): Flow<List<Materia>> =
        dataSource.obtenerMaterias()

    override suspend fun agregarMateria(
        nombre: String,
        profesor: String,
        colorHex: String
    ): Materia = dataSource.agregarMateria(nombre, profesor, colorHex)

    override suspend fun actualizarMateria(materia: Materia) =
        dataSource.actualizarMateria(materia)

    override suspend fun eliminarMateria(id: Int) =
        dataSource.eliminarMateria(id)
}