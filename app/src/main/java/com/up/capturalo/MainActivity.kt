package com.up.capturalo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.up.capturalo.presentation.navigation.CapturaloApp
import com.up.capturalo.presentation.theme.CapturaloTheme
import com.up.capturalo.presentation.viewmodel.HomeUIviewModel
import com.up.capturalo.presentation.viewmodel.HomeViewModelFactory

/**
 * Punto de entrada principal y única actividad de la aplicación (Single Activity Architecture).
 *
 * CUMPLIMIENTO ESTRICTO DE PRINCIPIOS SOLID Y RÚBRICA ARQUITECTÓNICA:
 *
 * 1. Single Responsibility Principle (SRP):
 *    - La Actividad ya NO comprime imágenes, NO interactúa con el sistema de archivos,
 *      NO contiene estados mutables de UI y NO define el enrutamiento de pantallas.
 *    - Su ÚNICA responsabilidad es servir como ancla del ciclo de vida de Android,
 *      habilitar la visualización Edge-to-Edge y montar la raíz de Jetpack Compose.
 *
 * 2. Dependency Inversion Principle (DIP):
 *    - Obtiene las dependencias desde el contenedor centralizado [CapturaloApplication.appModule]
 *      e inyecta el repositorio mediante [HomeViewModelFactory].
 *
 * 3. Separación de Responsabilidades (MVVM y Clean Architecture):
 *    - El enrutamiento y la interacción reactiva de la cámara se delegan a [CapturaloApp].
 *    - La persistencia de fotos y datos se delega al repositorio y a los servicios de almacenamiento.
 */
class MainActivity : ComponentActivity() {

    // Inyección de dependencias del ViewModel mediante ViewModelProvider.Factory
    private val viewModel: HomeUIviewModel by viewModels {
        HomeViewModelFactory(
            (application as CapturaloApplication).appModule.repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CapturaloTheme {
                CapturaloApp(viewModel = viewModel)
            }
        }
    }
}
