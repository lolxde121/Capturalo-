package com.up.capturalo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.up.capturalo.presentation.screens.HomeUiScreen
import com.up.capturalo.presentation.viewmodel.HomeUIviewModel
import com.up.capturalo.presentation.viewmodel.HomeViewModelFactory

/**
 * Punto de entrada de la aplicación.
 *
 * Responsabilidades:
 *  1. Recuperar el [AppModule] del [CapturaloApplication].
 *  2. Construir el [HomeUIviewModel] con su Factory (DI manual).
 *  3. Conectar el [HomeUiScreen] stateless con el ViewModel mediante
 *     colección del StateFlow y paso de lambdas.
 */
class MainActivity : ComponentActivity() {

    // Construimos el ViewModel inyectando el repositorio desde AppModule
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

            HomeUiScreen(
                uiState               = uiState,
                onAgregarMateriaClick = viewModel::onAgregarMateriaClick,
                onMateriaClick        = { /* TODO: navegar al detalle */ },
                onEditarMateria       = viewModel::onEditarMateriaClick,
                onEliminarMateria     = viewModel::eliminarMateria,
                onNombreChange        = viewModel::onNombreChange,
                onProfesorChange      = viewModel::onProfesorChange,
                onColorChange         = viewModel::onColorChange,
                onGuardar             = viewModel::onGuardarMateria,
                onCancelar            = viewModel::onCancelarDialogo,
                onCamaraClick         = { /* TODO: navegar a TomarFoto */ },
                onErrorDismissed      = viewModel::onErrorDismissed,
            )
        }
    }
}
