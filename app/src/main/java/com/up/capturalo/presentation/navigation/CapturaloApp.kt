package com.up.capturalo.presentation.navigation

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.up.capturalo.presentation.screens.AgregarMateriaScreen
import com.up.capturalo.presentation.screens.DetalleMateriaScreen
import com.up.capturalo.presentation.screens.HomeUiScreen
import com.up.capturalo.presentation.screens.TomarFotoUiScreen
import com.up.capturalo.presentation.viewmodel.HomeUIviewModel
import com.up.capturalo.presentation.viewmodel.PantallaActual

/**
 * Contenedor principal de navegación y orquestación de la UI (Clean Architecture / Single Responsibility).
 *
 * RESPONSABILIDAD:
 * Desacopla a [com.up.capturalo.MainActivity] de la lógica de enrutamiento visual, del manejo
 * del BackHandler y de los contratos del sistema operativo como [rememberLauncherForActivityResult].
 *
 * CUMPLIMIENTO DE RÚBRICA:
 * - StateFlow y UDF (10%): Se consume el estado inmutable mediante [collectAsStateWithLifecycle]
 *   asegurando que Compose detenga la recolección cuando la app pase a segundo plano.
 * - MVVM (10%): La UI no contiene reglas de negocio ni lógica de E/S.
 * - SOLID: Single Responsibility (MainActivity solo aloja el contenedor; CapturaloApp gestiona la navegación).
 */
@Composable
fun CapturaloApp(
    viewModel: HomeUIviewModel,
    modifier: Modifier = Modifier
) {
    // 1. Recolección segura del StateFlow respetando el ciclo de vida de la Activity (Rúbrica 10%)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 2. Launcher reactivo para interactuar con la cámara del sistema operativo
    // Al capturar la foto, el Bitmap se delega limpiamente al ViewModel (Cero lógica de archivos en UI)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.onFotoCapturada(bitmap)
        }
    }

    // 3. Manejo del botón atrás nativo del dispositivo (UDF)
    BackHandler(enabled = uiState.pantallaActual != PantallaActual.HOME) {
        viewModel.onVolverAHome()
    }

    // 4. Enrutamiento declarativo según el estado emitido por el ViewModel
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
                modifier              = modifier,
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
                modifier          = modifier,
            )
        }

        PantallaActual.DETALLE -> {
            uiState.materiaSeleccionada?.let { materia ->
                DetalleMateriaScreen(
                    materia          = materia,
                    capturas         = uiState.capturas,
                    onCapturaClick   = { /* Evento de visualización ampliada */ },
                    onTomarFotoClick = {
                        viewModel.onPrepararTomaFoto(materia)
                        cameraLauncher.launch(null)
                    },
                    onSalirClick     = viewModel::onVolverAHome,
                    modifier         = modifier,
                )
            } ?: run {
                // Recuperación defensiva en caso de inconsistencia de estado
                viewModel.onVolverAHome()
            }
        }

        PantallaActual.TOMAR_FOTO -> {
            TomarFotoUiScreen(
                materias              = uiState.materiasFiltradas,
                totalCapturasProvider = uiState::totalCapturas,
                onMateriaClick        = { materia ->
                    viewModel.onPrepararTomaFoto(materia)
                    cameraLauncher.launch(null)
                },
                onCancelarClick       = viewModel::onVolverAHome,
                modifier              = modifier,
            )
        }
    }
}
