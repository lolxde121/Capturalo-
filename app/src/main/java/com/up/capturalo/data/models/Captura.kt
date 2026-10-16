package com.up.capturalo.data.models

data class Captura(
    val id: Int,
    val idMateria: Int,
    val rutaImagen: String,
    val fecha: Long = System.currentTimeMillis()
)
