package com.up.capturalo.presentation.viewmodel

import com.up.capturalo.data.models.Captura
import com.up.capturalo.data.models.Materia

/**
 * Enumeración que representa la pantalla visible actualmente en la navegación de la aplicación.
 */
enum class PantallaActual {
    HOME,
    AGREGAR,
    DETALLE,
    TOMAR_FOTO
}

/**
 * Estado visual completo e inmutable de la aplicación (MVI / StateFlow).
 *
 * CONCEPTOS EDUCATIVOS:
 * 1. Single Source of Truth (Fuente única de la verdad):
 *    Todo el estado de la UI (datos, navegación, diálogos, formularios y estados de carga)
 *    está centralizado en esta data class inmutable.
 * 2. Unidirectional Data Flow (UDF):
 *    La UI observa este estado y emite eventos (lambdas) hacia el ViewModel; el ViewModel
 *    procesa la lógica de negocio y emite un nuevo `HomeUiState` mediante `copy()`.
 */
data class HomeUiState(
    /** Pantalla que se debe renderizar en la vista principal. */
    val pantallaActual: PantallaActual = PantallaActual.HOME,

    /** Lista de materias leídas en tiempo real desde DataStore. */
    val materias: List<Materia> = emptyList(),

    /** Materia seleccionada actualmente para visualizar en [DetalleMateriaScreen]. */
    val materiaSeleccionada: Materia? = null,

    /** Capturas asociadas a la materia seleccionada. */
    val capturas: List<Captura> = emptyList(),

    /** Registro de todas las capturas existentes en persistencia (para conteo en Home). */
    val todasLasCapturas: List<Captura> = emptyList(),

    /** Controla si el diálogo modal de Agregar/Editar en Home está visible. */
    val mostrarDialogo: Boolean = false,

    /**
     * Materia que se está editando. Si es `null`, opera en modo "Agregar".
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

    /** Indica si hay una operación asíncrona en curso. */
    val cargando: Boolean = false,
) {
    /** Obtiene el conteo exacto de capturas vinculadas a una materia específica */
    fun totalCapturas(idMateria: Int): Int = todasLasCapturas.count { it.idMateria == idMateria }
}

/** Paleta de colores predefinidos para el selector. */
val coloresDisponibles = listOf(
    "4CAF50", // Verde
    "2196F3", // Azul
    "9C27B0", // Morado
    "FF9800", // Naranja
    "F44336", // Rojo
    "00BCD4", // Cyan
)
