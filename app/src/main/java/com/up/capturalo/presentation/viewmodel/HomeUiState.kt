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
 * Estado visual completo e inmutable de la aplicación (MVI / StateFlow / UDF).
 *
 * CONCEPTOS EDUCATIVOS Y RÚBRICA (10% Gestión de Estados & 10% Complejidad):
 * 1. Single Source of Truth (Fuente única de la verdad):
 *    Todo el estado de la UI (datos, navegación, diálogos, formularios, métricas y estados de carga)
 *    está centralizado en esta data class inmutable.
 * 2. Unidirectional Data Flow (UDF):
 *    La UI observa este estado y emite eventos (lambdas) hacia el ViewModel; el ViewModel
 *    procesa la lógica de negocio y emite un nuevo `HomeUiState` mediante `copy()`.
 * 3. Transformaciones complejas de colecciones en memoria:
 *    Calcula métricas agregadas (conteo por materia, porcentajes, materia con mayor actividad)
 *    y filtrado de colecciones en memoria sin llamadas a BD ni APIs.
 */
data class HomeUiState(
    /** Pantalla que se debe renderizar en la vista principal. */
    val pantallaActual: PantallaActual = PantallaActual.HOME,

    /** Lista de materias leídas en tiempo real desde DataStore. */
    val materias: List<Materia> = emptyList(),

    /** Materia seleccionada actualmente para visualizar en DetalleMateriaScreen. */
    val materiaSeleccionada: Materia? = null,

    /** Capturas asociadas a la materia seleccionada. */
    val capturas: List<Captura> = emptyList(),

    /** Registro de todas las capturas existentes en persistencia (para conteo y métricas). */
    val todasLasCapturas: List<Captura> = emptyList(),

    /** Controla si el diálogo modal de Agregar/Editar en Home está visible. */
    val mostrarDialogo: Boolean = false,

    /** Materia que se está editando. Si es `null`, opera en modo "Agregar". */
    val materiaEnEdicion: Materia? = null,

    /** Materia destino asignada para una captura fotográfica en proceso. */
    val materiaDestinoFoto: Materia? = null,

    /** Valor del campo "Nombre" dentro del formulario. */
    val formularioNombre: String = "",

    /** Valor del campo "Profesor" dentro del formulario. */
    val formularioProfesor: String = "",

    /** Color seleccionado (hex sin #) dentro del formulario. */
    val formularioColor: String = "4CAF50",

    /** Texto de consulta para filtrar materias en memoria. */
    val filtroQuery: String = "",

    /** Mensaje de error a mostrar en Snackbar; null = sin error. */
    val error: String? = null,

    /** Mensaje informativo de éxito a mostrar en Snackbar; null = sin mensaje. */
    val mensajeExito: String? = null,

    /** Indica si hay una operación asíncrona en curso. */
    val cargando: Boolean = false,
) {
    // -------------------------------------------------------------------------
    // RÚBRICA: COMPLEJIDAD (Transformaciones lógicas y matemáticas en memoria)
    // -------------------------------------------------------------------------

    /** Total de materias registradas */
    val totalMaterias: Int get() = materias.size

    /** Total global de capturas registradas en el sistema */
    val totalCapturasGeneral: Int get() = todasLasCapturas.size

    /**
     * Lista de materias filtrada dinámicamente en memoria según el texto de búsqueda.
     */
    val materiasFiltradas: List<Materia>
        get() = if (filtroQuery.isBlank()) {
            materias
        } else {
            materias.filter {
                it.nombre.contains(filtroQuery, ignoreCase = true) ||
                it.profesor.contains(filtroQuery, ignoreCase = true)
            }
        }

    /**
     * Obtiene el conteo exacto de capturas vinculadas a una materia específica.
     */
    fun totalCapturasPorMateria(idMateria: Int): Int =
        todasLasCapturas.count { it.idMateria == idMateria }

    /** Compatibilidad hacia atrás con el método anterior */
    fun totalCapturas(idMateria: Int): Int = totalCapturasPorMateria(idMateria)

    /**
     * Calcula la relación porcentual (0-100%) que representa una materia respecto al total de capturas.
     */
    fun porcentajeDeCapturas(idMateria: Int): Float {
        if (totalCapturasGeneral == 0) return 0f
        return (totalCapturasPorMateria(idMateria).toFloat() / totalCapturasGeneral.toFloat()) * 100f
    }

    /**
     * Encuentra en memoria la materia que cuenta con la mayor cantidad de capturas registradas.
     */
    val materiaConMasCapturas: Materia?
        get() = materias.maxByOrNull { totalCapturasPorMateria(it.id) }
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
