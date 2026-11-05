package com.up.capturalo.data.storage

import android.graphics.Bitmap

/**
 * Contrato de abstracción para el almacenamiento de imágenes (SOLID - Principio de Inversión de Dependencias).
 *
 * La capa de presentación y el dominio no conocen los detalles del sistema de archivos de Android
 * ni si las fotos se guardan en almacenamiento interno, almacenamiento externo o memoria.
 */
interface ImageStorageService {

    /**
     * Comprime y guarda el [bitmap] en el almacenamiento privado local en un hilo secundario (IO).
     *
     * @param bitmap Imagen capturada por la cámara.
     * @param idMateria Identificador de la materia a la que pertenece la captura.
     * @return Ruta absoluta del archivo creado en disco.
     */
    suspend fun guardarFoto(bitmap: Bitmap, idMateria: Int): String

    /**
     * Elimina el archivo de imagen de disco si existe.
     *
     * @param rutaImagen Ruta absoluta del archivo a eliminar.
     * @return `true` si el archivo fue eliminado con éxito, `false` en caso contrario.
     */
    suspend fun eliminarFoto(rutaImagen: String): Boolean
}
