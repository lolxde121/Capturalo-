package com.up.capturalo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.up.capturalo.data.models.Materia
import com.up.capturalo.data.repository.CapturaloRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel principal de la aplicación "Captúralo!".
 *
 * CONCEPTOS EDUCATIVOS Y ARQUITECTÓNICOS:
 * 1. Clean Architecture y MVVM:
 *    - Este ViewModel pertenece a la capa de Presentación y actúa como puente entre
 *      la capa de Datos/Dominio ([CapturaloRepository]) y la UI de Jetpack Compose.
 *    - No conoce nada del framework Android UI (cero dependencias de Android View, Context o Composables),
 *      lo que permite pruebas unitarias (Unit Tests) limpias y rápidas en la JVM.
 *
 * 2. Inyección de Dependencias:
 *    - Recibe el repositorio [CapturaloRepository] por constructor. Esto cumple con el
 *      Principio de Inversión de Dependencias (SOLID): depende de una abstracción (interfaz),
 *      no de una implementación concreta.
 *
 * 3. StateFlow y Unidirectional Data Flow (UDF):
 *    - Mantiene el estado mutable privado [_uiState] y expone una versión inmutable [uiState]
 *      como [StateFlow] para que la UI observe los cambios de manera reactiva y segura para hilos.
 *
 * 4. Kotlin Coroutines ([viewModelScope]):
 *    - Todas las llamadas asíncronas y operaciones de disco se lanzan dentro de [viewModelScope],
 *      asegurando que si el ViewModel es destruido, los trabajos se cancelen automáticamente
 *      evitando fugas de memoria (memory leaks).
 */
class HomeUIviewModel(
    private val repository: CapturaloRepository
) : ViewModel() {

    // Fuente única de la verdad del estado de la pantalla
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Job para cancelar la observación anterior de capturas si se cambia de materia
    private var capturasJob: Job? = null

    init {
        // Al instanciar el ViewModel observamos materias y capturas en tiempo real
        observarMaterias()
        observarTodasLasCapturas()
    }

    // -------------------------------------------------------------------------
    // Observación Reactiva de Datos
    // -------------------------------------------------------------------------

    /**
     * Se suscribe al [Flow] de materias emitido por el repositorio.
     * Cada vez que DataStore se actualiza en disco, el Flow emite una nueva lista
     * y el estado de la UI se actualiza automáticamente.
     */
    private fun observarMaterias() {
        repository.obtenerMaterias()
            .onEach { lista ->
                _uiState.update { it.copy(materias = lista, cargando = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(error = e.message, cargando = false) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Observa todas las capturas persistidas para mantener actualizados los contadores de la UI.
     */
    private fun observarTodasLasCapturas() {
        repository.obtenerTodasLasCapturas()
            .onEach { lista ->
                _uiState.update { it.copy(todasLasCapturas = lista) }
            }
            .catch { e ->
                _uiState.update { it.copy(error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Observa las capturas correspondientes a la materia con ID [idMateria].
     */
    private fun observarCapturasDeMateria(idMateria: Int) {
        capturasJob?.cancel()
        capturasJob = repository.obtenerCapturasPorMateria(idMateria)
            .onEach { listaCapturas ->
                _uiState.update { it.copy(capturas = listaCapturas) }
            }
            .catch { e ->
                _uiState.update { it.copy(error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    // -------------------------------------------------------------------------
    // Navegación entre Pantallas
    // -------------------------------------------------------------------------

    /**
     * Navega a la pantalla completa [com.up.capturalo.presentation.screens.AgregarMateriaScreen]
     * y reinicia los campos del formulario.
     */
    fun onNavegarAAgregarMateria() {
        _uiState.update {
            it.copy(
                pantallaActual     = PantallaActual.AGREGAR,
                materiaEnEdicion   = null,
                formularioNombre   = "",
                formularioProfesor = "",
                formularioColor    = coloresDisponibles.first(),
                error              = null,
            )
        }
    }

    /**
     * Navega a la pantalla [com.up.capturalo.presentation.screens.DetalleMateriaScreen]
     * cargando la materia indicada y comenzando a observar sus capturas.
     */
    fun onSeleccionarMateriaDetalle(materia: Materia) {
        _uiState.update {
            it.copy(
                pantallaActual      = PantallaActual.DETALLE,
                materiaSeleccionada = materia,
                error               = null,
            )
        }
        observarCapturasDeMateria(materia.id)
    }

    /**
     * Navega a la pantalla [com.up.capturalo.presentation.screens.TomarFotoUiScreen]
     * para seleccionar a qué materia asignar una nueva captura.
     */
    fun onNavegarATomarFoto() {
        _uiState.update {
            it.copy(
                pantallaActual = PantallaActual.TOMAR_FOTO,
                error          = null,
            )
        }
    }

    /**
     * Regresa a la pantalla principal [com.up.capturalo.presentation.screens.HomeUiScreen].
     */
    fun onVolverAHome() {
        _uiState.update {
            it.copy(
                pantallaActual      = PantallaActual.HOME,
                materiaSeleccionada = null,
                mostrarDialogo      = false,
                error               = null,
            )
        }
    }

    // -------------------------------------------------------------------------
    // Eventos de Diálogo en Home (Agregar / Editar rápido)
    // -------------------------------------------------------------------------

    /** Abre el diálogo modal en modo "Agregar". */
    fun onAgregarMateriaClick() {
        _uiState.update {
            it.copy(
                mostrarDialogo     = true,
                materiaEnEdicion   = null,
                formularioNombre   = "",
                formularioProfesor = "",
                formularioColor    = coloresDisponibles.first(),
                error              = null,
            )
        }
    }

    /** Abre el diálogo modal en modo "Editar" precargando los datos de [materia]. */
    fun onEditarMateriaClick(materia: Materia) {
        _uiState.update {
            it.copy(
                mostrarDialogo     = true,
                materiaEnEdicion   = materia,
                formularioNombre   = materia.nombre,
                formularioProfesor = materia.profesor,
                formularioColor    = materia.color,
                error              = null,
            )
        }
    }

    /** Cierra el diálogo modal sin guardar cambios. */
    fun onCancelarDialogo() {
        _uiState.update { it.copy(mostrarDialogo = false, error = null) }
    }

    // -------------------------------------------------------------------------
    // Gestión de Campos de Entrada del Formulario
    // -------------------------------------------------------------------------

    fun onNombreChange(value: String) =
        _uiState.update { it.copy(formularioNombre = value) }

    fun onProfesorChange(value: String) =
        _uiState.update { it.copy(formularioProfesor = value) }

    fun onColorChange(colorHex: String) =
        _uiState.update { it.copy(formularioColor = colorHex) }

    // -------------------------------------------------------------------------
    // Operaciones CRUD de Materias
    // -------------------------------------------------------------------------

    /**
     * Valida y guarda la materia en persistencia (DataStore):
     * - Si [HomeUiState.materiaEnEdicion] es null, se crea una nueva materia (CREATE).
     * - Si no es null, se actualiza la materia existente (UPDATE).
     *
     * Tras completar la operación, regresa al Home y limpia el estado del formulario.
     */
    fun onGuardarMateria() {
        val estado = _uiState.value

        // Validación de datos requeridos
        if (estado.formularioNombre.isBlank()) {
            _uiState.update { it.copy(error = "El nombre de la materia no puede estar vacío.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                if (estado.materiaEnEdicion == null) {
                    // CREATE: Agrega nueva materia con ID auto-generado en DataStore
                    repository.agregarMateria(
                        nombre    = estado.formularioNombre.trim(),
                        profesor  = estado.formularioProfesor.trim(),
                        colorHex  = estado.formularioColor,
                    )
                } else {
                    // UPDATE: Actualiza campos de la materia existente
                    repository.actualizarMateria(
                        estado.materiaEnEdicion.copy(
                            nombre   = estado.formularioNombre.trim(),
                            profesor = estado.formularioProfesor.trim(),
                            color    = estado.formularioColor,
                        )
                    )
                }

                // Éxito: volver a Home, cerrar diálogo y resetear campos
                _uiState.update {
                    it.copy(
                        pantallaActual     = PantallaActual.HOME,
                        mostrarDialogo     = false,
                        materiaEnEdicion   = null,
                        formularioNombre   = "",
                        formularioProfesor = "",
                        error              = null,
                        cargando           = false,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error    = e.message ?: "Ocurrió un error al guardar la materia.",
                        cargando = false
                    )
                }
            }
        }
    }

    /**
     * Elimina del medio persistente la materia con el [id] proporcionado (DELETE).
     */
    fun eliminarMateria(id: Int) {
        viewModelScope.launch {
            try {
                repository.eliminarMateria(id)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error al eliminar la materia.")
                }
            }
        }
    }

    /**
     * Registra una nueva captura fotográfica asociada a la materia con ID [idMateria]
     * y persiste el cambio inmediatamente en DataStore.
     */
    fun agregarCaptura(idMateria: Int, rutaImagen: String) {
        viewModelScope.launch {
            try {
                repository.agregarCaptura(idMateria, rutaImagen)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error al registrar la captura fotográfica.")
                }
            }
        }
    }

    /**
     * Elimina una captura fotográfica por su [id].
     */
    fun eliminarCaptura(id: Int) {
        viewModelScope.launch {
            try {
                repository.eliminarCaptura(id)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error al eliminar la captura.")
                }
            }
        }
    }

    /**
     * Limpia el mensaje de error activo una vez que ha sido notificado al usuario.
     */
    fun onErrorDismissed() {
        _uiState.update { it.copy(error = null) }
    }
}