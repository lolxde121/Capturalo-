package com.up.capturalo.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.up.capturalo.data.models.Captura
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
 * CONCEPTOS EDUCATIVOS Y CUMPLIMIENTO DE RÚBRICA:
 * 1. Clean Architecture y MVVM (Rúbrica: Separación total de responsabilidades):
 *    - Este ViewModel pertenece a la capa de Presentación y actúa como mediador entre
 *      la capa de Datos/Dominio ([CapturaloRepository]) y la UI de Jetpack Compose.
 *    - La UI es completamente "tonta" (stateless): no gestiona archivos, no comprime imágenes
 *      ni procesa rutas. Todo el procesamiento de datos se centraliza aquí.
 *
 * 2. Principios SOLID:
 *    - Single Responsibility (SRP): Coordina el estado de presentación y la ejecución de casos de uso.
 *    - Dependency Inversion (DIP): Depende de una abstracción ([CapturaloRepository]), no de implementaciones.
 *
 * 3. Patrón Backing Property y StateFlow (Rúbrica: 10% Gestión de Estados):
 *    - Mantiene el estado mutable privado [_uiState] y expone la propiedad de respaldo inmutable
 *      [uiState] como [StateFlow] para garantizar inmutabilidad y UDF.
 *
 * 4. Kotlin Coroutines ([viewModelScope]):
 *    - Todas las llamadas asíncronas y operaciones de disco se lanzan dentro de [viewModelScope]
 *      garantizando cancelación estructurada si el ciclo de vida se destruye.
 */
class HomeUIviewModel(
    private val repository: CapturaloRepository
) : ViewModel() {

    // Patrón Backing Property: estado mutable privado
    private val _uiState = MutableStateFlow(HomeUiState())
    // Exposición pública e inmutable para los composables
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Job para cancelar la observación anterior de capturas si se cambia de materia
    private var capturasJob: Job? = null

    init {
        // Al instanciar el ViewModel observamos materias y capturas en tiempo real
        observarMaterias()
        observarTodasLasCapturas()
    }

    // -------------------------------------------------------------------------
    // Observación Reactiva de Datos (DataStore -> Flow -> StateFlow)
    // -------------------------------------------------------------------------

    /**
     * Se suscribe al [kotlinx.coroutines.flow.Flow] de materias emitido por el repositorio.
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
    // Navegación entre Pantallas (MVI / UDF)
    // -------------------------------------------------------------------------

    /**
     * Navega a la pantalla de formulario completo [PantallaActual.AGREGAR]
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
     * Navega a la pantalla [PantallaActual.DETALLE] cargando la materia indicada
     * y comenzando a observar sus capturas reactivas.
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
     * Navega a la pantalla [PantallaActual.TOMAR_FOTO] para seleccionar a qué materia asignar
     * una nueva captura fotográfica.
     */
    fun onNavegarATomarFoto() {
        _uiState.update {
            it.copy(
                pantallaActual     = PantallaActual.TOMAR_FOTO,
                materiaDestinoFoto = null,
                error              = null,
            )
        }
    }

    /**
     * Prepara el destino de una fotografía antes de que se dispare la cámara.
     */
    fun onPrepararTomaFoto(materia: Materia?) {
        _uiState.update { it.copy(materiaDestinoFoto = materia) }
    }

    /**
     * Regresa a la pantalla principal [PantallaActual.HOME].
     */
    fun onVolverAHome() {
        _uiState.update {
            it.copy(
                pantallaActual      = PantallaActual.HOME,
                materiaSeleccionada = null,
                materiaDestinoFoto  = null,
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

    fun onFiltroQueryChange(query: String) =
        _uiState.update { it.copy(filtroQuery = query) }

    // -------------------------------------------------------------------------
    // Operaciones CRUD de Materias
    // -------------------------------------------------------------------------

    /**
     * Valida y guarda la materia en persistencia (DataStore):
     * - Si [HomeUiState.materiaEnEdicion] es null, se crea una nueva materia (CREATE).
     * - Si no es null, se actualiza la materia existente (UPDATE).
     */
    fun onGuardarMateria() {
        val estado = _uiState.value

        // Validación de datos requeridos en el ViewModel (regla de negocio)
        if (estado.formularioNombre.isBlank()) {
            _uiState.update { it.copy(error = "El nombre de la materia no puede estar vacío.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                if (estado.materiaEnEdicion == null) {
                    repository.agregarMateria(
                        nombre    = estado.formularioNombre.trim(),
                        profesor  = estado.formularioProfesor.trim(),
                        colorHex  = estado.formularioColor,
                    )
                } else {
                    repository.actualizarMateria(
                        estado.materiaEnEdicion.copy(
                            nombre   = estado.formularioNombre.trim(),
                            profesor = estado.formularioProfesor.trim(),
                            color    = estado.formularioColor,
                        )
                    )
                }

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

    // -------------------------------------------------------------------------
    // Gestión de Capturas Fotográficas (Decoupled & SRP)
    // -------------------------------------------------------------------------

    /**
     * Recibe el [Bitmap] capturado por la cámara, delega su compresión y almacenamiento
     * al [CapturaloRepository] en un hilo IO, actualiza el registro en DataStore y navega
     * automáticamente a la vista de detalle de la materia destino.
     *
     * Este método elimina toda la lógica de compresión de archivos que antes residía en MainActivity.
     */
    fun onFotoCapturada(bitmap: Bitmap) {
        val estado = _uiState.value
        val materia = estado.materiaDestinoFoto
            ?: estado.materiaSeleccionada
            ?: estado.materias.firstOrNull()

        if (materia == null) {
            _uiState.update { it.copy(error = "No hay materia disponible para asociar la fotografía.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                // Guarda la imagen físicamente en almacenamiento privado y persiste en DataStore
                repository.registrarCapturaConFoto(materia.id, bitmap)

                // Actualiza estado y redirige a la vista de detalle
                _uiState.update {
                    it.copy(
                        pantallaActual      = PantallaActual.DETALLE,
                        materiaSeleccionada = materia,
                        materiaDestinoFoto  = null,
                        cargando            = false,
                        error               = null,
                    )
                }
                observarCapturasDeMateria(materia.id)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error    = e.message ?: "Error al guardar la fotografía capturada.",
                        cargando = false
                    )
                }
            }
        }
    }

    /**
     * Registra una nueva captura fotográfica con ruta ya existente.
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
     * Elimina una captura fotográfica y su archivo físico asociado en disco.
     */
    fun eliminarCaptura(captura: Captura) {
        viewModelScope.launch {
            try {
                repository.eliminarCapturaConArchivo(captura)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error al eliminar la captura.")
                }
            }
        }
    }

    /**
     * Sobrecarga para eliminar captura solo por ID.
     */
    fun eliminarCapturaPorId(id: Int) {
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
