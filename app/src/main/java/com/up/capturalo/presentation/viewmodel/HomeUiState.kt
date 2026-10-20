package com.up.capturalo.presentation.viewmodel

import com.up.capturalo.data.models.Materia

/**
 * Estado visual completo de la pantalla Home.
 * El ViewModel siempre emite un nuevo objeto inmutable;
 * la UI sólo recompone lo que cambió.
 */
data class HomeUiState(
    /** Lista de materias a mostrar. */
    val materias: List<Materia> = emptyList(),

    /** Controla si el diálogo de Agregar/Editar está visible. */
    val mostrarDialogo: Boolean = false,

    /**
     * Materia que se está editando. Si es `null`, el diálogo
     * opera en modo "Agregar".
     */
    val materiaEnEdicion: Materia? = null,

    /** Valor del campo "Nombre" dentro del formulario. */
    val formularioNombre: String = "",

    /** Valor del campo "Profesor" dentro del formulario. */
    val formularioProfesor: String = "",

    /** Color seleccionado (hex sin #) dentro del formulario. */
    val formularioColor: String = "4CAF50",

    /** Mensaje de error a mostrar en Snackbar; null = sin error. */
    val error: String? = null,

    /** Indica si hay una operación en curso. */
    val cargando: Boolean = false,
)

/** Paleta de colores predefinidos para el selector. */
val coloresDisponibles = listOf(
    "4CAF50", // Verde
    "2196F3", // Azul
    "9C27B0", // Morado
    "FF9800", // Naranja
    "F44336", // Rojo
    "00BCD4", // Cyan
)
