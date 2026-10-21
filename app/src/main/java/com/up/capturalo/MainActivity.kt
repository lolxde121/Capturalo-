package com.up.capturalo

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.up.capturalo.data.models.Materia
import com.up.capturalo.presentation.screens.AgregarMateriaScreen
import com.up.capturalo.presentation.screens.DetalleMateriaScreen
import com.up.capturalo.presentation.screens.HomeUiScreen
import com.up.capturalo.presentation.screens.TomarFotoUiScreen
import com.up.capturalo.presentation.viewmodel.HomeUIviewModel
import com.up.capturalo.presentation.viewmodel.HomeViewModelFactory
import com.up.capturalo.presentation.viewmodel.PantallaActual
import java.io.File
import java.io.FileOutputStream

/**
 * Punto de entrada principal y única actividad de la aplicación (Single Activity Architecture).
 *
 * CONCEPTOS EDUCATIVOS Y ARQUITECTÓNICOS:
 * 1. Clean Architecture en Android:
 *    - La Actividad pertenece a la Capa de Presentación más externa.
 *    - No contiene lógica de negocio, no conoce DataStore ni bases de datos.
 *    - Su única función es alojar el contenedor Jetpack Compose y conectar el estado
 *      del ViewModel con las pantallas correspondientes.
 *
 * 2. Inyección de Dependencias Manual (Manual Dependency Injection):
 *    - Accedemos al contenedor [com.up.capturalo.di.AppModule] provisto a nivel de
 *      aplicación por [CapturaloApplication].
 *    - Usamos [HomeViewModelFactory] para instanciar [HomeUIviewModel] pasando el
 *      repositorio [com.up.capturalo.data.repository.CapturaloRepository].
 *
 * 3. State Hoisting y Navegación Reactiva:
 *    - El estado completo se observa mediante `viewModel.uiState.collectAsState()`.
 *    - Dependiendo del valor de [PantallaActual], se despliega:
 *      a) [HomeUiScreen]: Lista de materias con tarjetas y opciones de agregar/editar/eliminar.
 *      b) [AgregarMateriaScreen]: Formulario completo para registrar una nueva materia.
 *      c) [DetalleMateriaScreen]: Vista detallada de la materia seleccionada con su galería de capturas.
 *      d) [TomarFotoUiScreen]: Selector de materia destino para tomar y asignar una nueva foto.
 *    - [BackHandler] intercepta el gesto/botón Atrás del sistema operativo para retornar fluidamente al Home.
 *
 * 4. Integración de Cámara en Jetpack Compose:
 *    - Se utiliza `rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview())`
 *      que abre la aplicación de cámara del dispositivo/emulador y retorna el Bitmap capturado.
 *    - La imagen se persiste en el almacenamiento interno privado (`filesDir/capturas`) y su ruta
 *      se registra de forma asíncrona en DataStore a través del ViewModel.
 */
class MainActivity : ComponentActivity() {

    // Instancia del ViewModel inyectando el repositorio mediante ViewModelProvider.Factory
    private val viewModel: HomeUIviewModel by viewModels {
        HomeViewModelFactory(
            (application as CapturaloApplication).appModule.repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val context = LocalContext.current

            // Referencia a la materia a la que se le asignará la foto que se está tomando
            var materiaDestinoFoto by remember { mutableStateOf<Materia?>(null) }

            // Launcher reactivo de cámara para emulador y dispositivo real
            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview()
            ) { bitmap: Bitmap? ->
                if (bitmap != null) {
                    val materia = materiaDestinoFoto
                        ?: uiState.materiaSeleccionada
                        ?: uiState.materias.firstOrNull()

                    if (materia != null) {
                        try {
                            // Directorio privado para guardar las fotos capturadas
                            val carpeta = File(context.filesDir, "capturas").apply { mkdirs() }
                            val nombreArchivo = "foto_${materia.id}_${System.currentTimeMillis()}.jpg"
                            val archivo = File(carpeta, nombreArchivo)

                            FileOutputStream(archivo).use { out ->
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                            }

                            // Registra la nueva captura en DataStore mediante el ViewModel
                            viewModel.agregarCaptura(materia.id, archivo.absolutePath)

                            // Navega a la vista de detalle de esa materia para ver la foto de inmediato
                            viewModel.onSeleccionarMateriaDetalle(materia)
                        } catch (e: Exception) {
                            // Manejo seguro en caso de error de E/S
                        }
                    }
                }
            }

            // Manejo del botón atrás del sistema operativo cuando estamos fuera de Home
            BackHandler(enabled = uiState.pantallaActual != PantallaActual.HOME) {
                viewModel.onVolverAHome()
            }

            // Router reactivo basado en el estado emitido por el ViewModel
            when (uiState.pantallaActual) {
                PantallaActual.HOME -> {
                    HomeUiScreen(
                        uiState               = uiState,
                        onAgregarMateriaClick = viewModel::onNavegarAAgregarMateria,
                        onMateriaClick        = viewModel::onSeleccionarMateriaDetalle,
                        onEditarMateria       = viewModel::onEditarMateriaClick,
                        onEliminarMateria     = viewModel::eliminarMateria,
                        onNombreChange        = viewModel::onNombreChange,
                        onProfesorChange      = viewModel::onProfesorChange,
                        onColorChange         = viewModel::onColorChange,
                        onGuardar             = viewModel::onGuardarMateria,
                        onCancelar            = viewModel::onCancelarDialogo,
                        onCamaraClick         = viewModel::onNavegarATomarFoto,
                        onErrorDismissed      = viewModel::onErrorDismissed,
                    )
                }

                PantallaActual.AGREGAR -> {
                    AgregarMateriaScreen(
                        nombre            = uiState.formularioNombre,
                        profesor          = uiState.formularioProfesor,
                        colorSeleccionado = uiState.formularioColor,
                        onNombreChange    = viewModel::onNombreChange,
                        onProfesorChange  = viewModel::onProfesorChange,
                        onColorClick      = viewModel::onColorChange,
                        onGuardarClick    = viewModel::onGuardarMateria,
                        onCancelarClick   = viewModel::onVolverAHome,
                    )
                }

                PantallaActual.DETALLE -> {
                    uiState.materiaSeleccionada?.let { materia ->
                        DetalleMateriaScreen(
                            materia          = materia,
                            capturas         = uiState.capturas,
                            onCapturaClick   = { /* Click en captura individual */ },
                            onTomarFotoClick = {
                                materiaDestinoFoto = materia
                                cameraLauncher.launch(null)
                            },
                            onSalirClick     = viewModel::onVolverAHome,
                        )
                    } ?: run {
                        // Si por algún motivo no hay materia seleccionada, vuelve de forma segura a Home
                        viewModel.onVolverAHome()
                    }
                }

                PantallaActual.TOMAR_FOTO -> {
                    TomarFotoUiScreen(
                        materias              = uiState.materias,
                        totalCapturasProvider = uiState::totalCapturas,
                        onMateriaClick        = { materia ->
                            materiaDestinoFoto = materia
                            cameraLauncher.launch(null)
                        },
                        onCancelarClick       = viewModel::onVolverAHome,
                    )
                }
            }
        }
    }
}
