package com.up.capturalo.utils

import androidx.compose.ui.graphics.Color

fun String.toComposeColor(): Color {
    val hex = this.removePrefix("#")
    val longColor = when (hex.length) {
        6 -> "FF$hex" // Si no trae alfa, le agregamos opacidad 100% (FF)
        8 -> hex     // Ya incluye alfa
        else -> "FFFFFFFF" // Blanco por defecto en caso of error
    }
    return Color(longColor.toLong(radix = 16).toInt())
}
