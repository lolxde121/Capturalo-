package com.up.capturalo.data.repository

import android.graphics.Bitmap
import com.up.capturalo.data.dataSource.CapturaloDataSource
import com.up.capturalo.data.models.Captura
import com.up.capturalo.data.models.Materia
import com.up.capturalo.data.storage.ImageStorageService
import kotlinx.coroutines.flow.Flow

/**
 * Implementación concreta de [CapturaloRepository].
 *
 * CONCEPTOS EDUCATIVOS Y SOLID:
 * 1. Mediador de la Capa de Datos (SRP):
 *    Coordina las operaciones entre la persistencia de metadatos ([CapturaloDataSource] con DataStore)
 *    y la persistencia física de archivos ([ImageStorageService] en disco).
 *
 * 2. Inversión de Dependencias (DIP):
 *    Recibe sus dependencias por constructor a través de abstracciones (`ImageStorageService`),
 *    facilitando el reemplazo en tests unitarios por fakes o mocks.
 *
 * 3. Ocultamiento de Detalles Técnicos:
 *    El repositorio es el "escudo" arquitectónico. Ni `Context`, ni `DataStore`,
 *    ni `Preferences`, ni `Gson`, ni `FileOutputStream` escapan de esta capa.
 */
class CapturaloRepositoryImp(
    private val dataSource: CapturaloDataSource,
    private val imageStorageService: ImageStorageService,
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

    override suspend fun registrarCapturaConFoto(idMateria: Int, bitmap: Bitmap): Captura {
        // 1. Guardar físicamente el bitmap en almacenamiento interno (Dispatchers.IO)
        val rutaArchivo = imageStorageService.guardarFoto(bitmap, idMateria)
        // 2. Persistir el registro con el ID autoincremental en Jetpack DataStore
        return dataSource.agregarCaptura(idMateria, rutaArchivo)
    }

    override suspend fun eliminarCaptura(id: Int) =
        dataSource.eliminarCaptura(id)

    override suspend fun eliminarCapturaConArchivo(captura: Captura) {
        // Eliminar el archivo físico de la memoria si existe
        if (captura.rutaImagen.isNotBlank()) {
            imageStorageService.eliminarFoto(captura.rutaImagen)
        }
        // Eliminar el registro de DataStore
        dataSource.eliminarCaptura(captura.id)
    }
}
