package com.up.capturalo.data.repository

import com.up.capturalo.data.dataSource.CapturaloDataSource
import com.up.capturalo.data.models.Captura
import com.up.capturalo.data.models.Materia
import kotlinx.coroutines.flow.Flow

/**
 * Implementación concreta de [CapturaloRepository].
 *
 * CONCEPTOS EDUCATIVOS:
 * 1. Mediador de la Capa de Datos:
 *    Esta clase implementa las operaciones del contrato conectándose directamente
 *    con el DataSource correspondiente (`CapturaloDataSource`).
 *
 * 2. Ocultamiento de Detalles Técnicos:
 *    El repositorio es el "escudo" arquitectónico. Ni `Context`, ni `DataStore`,
 *    ni `Preferences`, ni `Gson` escapan de esta capa. El resto de la app
 *    solo maneja objetos de negocio puros (`Materia`, `Flow<List<Materia>>`).
 */
class CapturaloRepositoryImp(
    private val dataSource: CapturaloDataSource
) : CapturaloRepository {

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

    override fun obtenerCapturasPorMateria(idMateria: Int): Flow<List<Captura>> =
        dataSource.obtenerCapturasPorMateria(idMateria)

    override fun obtenerTodasLasCapturas(): Flow<List<Captura>> =
        dataSource.obtenerTodasLasCapturas()

    override suspend fun agregarCaptura(idMateria: Int, rutaImagen: String): Captura =
        dataSource.agregarCaptura(idMateria, rutaImagen)

    override suspend fun eliminarCaptura(id: Int) =
        dataSource.eliminarCaptura(id)
}