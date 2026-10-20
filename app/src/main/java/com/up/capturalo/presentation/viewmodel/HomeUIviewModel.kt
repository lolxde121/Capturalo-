package com.up.capturalo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.up.capturalo.data.models.Materia
import com.up.capturalo.data.repository.CapturaloRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla principal.
 *
 * - Recibe [CapturaloRepository] por constructor (inyección manual vía Factory).
 * - Expone [uiState] como [StateFlow] inmutable hacia la UI.
 * - Todas las operaciones de escritura son `suspend` y se lanzan en [viewModelScope].
 */
class HomeUIviewModel(
    private val repository: CapturaloRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observarMaterias()
    }

    // -------------------------------------------------------------------------
    // Observación reactiva
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Eventos de UI → apertura/cierre de formulario
    // -------------------------------------------------------------------------

    /** Abre el diálogo en modo "Agregar". */
    fun onAgregarMateriaClick() {
        _uiState.update {
            it.copy(
                mostrarDialogo    = true,
                materiaEnEdicion  = null,
                formularioNombre  = "",
                formularioProfesor = "",
                formularioColor   = coloresDisponibles.first(),
                error             = null,
            )
        }
    }

    /** Abre el diálogo en modo "Editar" pre-cargando los datos de [materia]. */
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

    /** Cierra el diálogo sin guardar. */
    fun onCancelarDialogo() {
        _uiState.update { it.copy(mostrarDialogo = false, error = null) }
    }

    // -------------------------------------------------------------------------
    // Cambios de campos del formulario
    // -------------------------------------------------------------------------

    fun onNombreChange(value: String) =
        _uiState.update { it.copy(formularioNombre = value) }

    fun onProfesorChange(value: String) =
        _uiState.update { it.copy(formularioProfesor = value) }

    fun onColorChange(colorHex: String) =
        _uiState.update { it.copy(formularioColor = colorHex) }

    // -------------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------------

    /** Guarda la materia (agrega o actualiza según [HomeUiState.materiaEnEdicion]). */
    fun onGuardarMateria() {
        val estado = _uiState.value

        // Validación simple
        if (estado.formularioNombre.isBlank()) {
            _uiState.update { it.copy(error = "El nombre no puede estar vacío.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                if (estado.materiaEnEdicion == null) {
                    // CREATE
                    repository.agregarMateria(
                        nombre    = estado.formularioNombre.trim(),
                        profesor  = estado.formularioProfesor.trim(),
                        colorHex  = estado.formularioColor,
                    )
                } else {
                    // UPDATE
                    repository.actualizarMateria(
                        estado.materiaEnEdicion.copy(
                            nombre   = estado.formularioNombre.trim(),
                            profesor = estado.formularioProfesor.trim(),
                            color    = estado.formularioColor,
                        )
                    )
                }
                _uiState.update { it.copy(mostrarDialogo = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, cargando = false) }
            }
        }
    }

    /** Elimina la materia con el [id] dado. */
    fun eliminarMateria(id: Int) {
        viewModelScope.launch {
            try {
                repository.eliminarMateria(id)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    /** Limpia el mensaje de error (p.ej. tras mostrarlo en Snackbar). */
    fun onErrorDismissed() {
        _uiState.update { it.copy(error = null) }
    }
}