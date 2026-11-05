package com.up.capturalo.data.storage

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Implementación concreta de [ImageStorageService] utilizando el almacenamiento privado interno
 * de la aplicación (`context.filesDir/capturas`).
 *
 * Cumple con los principios SOLID:
 * - Single Responsibility Principle (SRP): Su única responsabilidad es el manejo físico de archivos
 *   de imagen en disco (creación de directorios, compresión y eliminación).
 * - Dependency Inversion Principle (DIP): Implementa una interfaz para que los consumidores dependan
 *   de abstracciones y no de esta clase concreta.
 *
 * Todas las operaciones de Entrada/Salida (E/S) se despachan de forma asíncrona en [Dispatchers.IO]
 * para garantizar que el hilo principal (Main/UI Thread) nunca se bloquee.
 */
class InternalStorageImageService(
    private val context: Context
) : ImageStorageService {

    companion object {
        private const val DIRECTORY_NAME = "capturas"
        private const val COMPRESSION_QUALITY = 90
    }

    override suspend fun guardarFoto(bitmap: Bitmap, idMateria: Int): String = withContext(Dispatchers.IO) {
        val carpeta = File(context.filesDir, DIRECTORY_NAME)
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw IOException("No se pudo crear el directorio de capturas: ${carpeta.absolutePath}")
        }

        val nombreArchivo = "foto_${idMateria}_${System.currentTimeMillis()}.jpg"
        val archivo = File(carpeta, nombreArchivo)

        FileOutputStream(archivo).use { outStream ->
            val exito = bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outStream)
            if (!exito) {
                throw IOException("Error al comprimir el bitmap a formato JPEG.")
            }
        }

        archivo.absolutePath
    }

    override suspend fun eliminarFoto(rutaImagen: String): Boolean = withContext(Dispatchers.IO) {
        if (rutaImagen.isBlank()) return@withContext false
        try {
            val archivo = File(rutaImagen)
            if (archivo.exists()) {
                archivo.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
