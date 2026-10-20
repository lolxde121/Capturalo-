package com.up.capturalo.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.up.capturalo.data.models.Materia
import com.up.capturalo.presentation.components.BotonAgregarMateria
import com.up.capturalo.presentation.components.BotonCamara
import com.up.capturalo.presentation.components.DialogoMateria
import com.up.capturalo.presentation.components.PildoraGris
import com.up.capturalo.presentation.components.TarjetaMateria
import com.up.capturalo.presentation.components.TopBarBlue
import com.up.capturalo.presentation.viewmodel.HomeUiState

/**
 * Pantalla principal 100% stateless.
 *
 * Recibe el [HomeUiState] del ViewModel y emite eventos mediante lambdas.
 * No contiene lógica de negocio ni accede a ningún ViewModel directamente.
 */
@Composable
fun HomeUiScreen(
    uiState: HomeUiState,
    onAgregarMateriaClick: () -> Unit,
    onMateriaClick: (Materia) -> Unit,
    onEditarMateria: (Materia) -> Unit,
    onEliminarMateria: (Int) -> Unit,
    onNombreChange: (String) -> Unit,
    onProfesorChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
    onCamaraClick: () -> Unit,
    onErrorDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar errores en Snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onErrorDismissed()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopBarBlue("Capturalo!")
            }
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            Column(
                modifier            = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Botón de agregar
                BotonAgregarMateria(onClick = onAgregarMateriaClick)

                // Encabezado de sección
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(
                        text       = "Mis Materias",
                        color      = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 12.sp,
                        style      = MaterialTheme.typography.titleLarge,
                    )
                    PildoraGris(
                        modifier = Modifier.width(100.dp),
                        texto    = "Activas",
                    )
                }

                // Grid de materias
                LazyVerticalGrid(
                    columns  = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                ) {
                    items(uiState.materias.size) { index ->
                        val materia = uiState.materias[index]
                        TarjetaMateria(
                            materia       = materia,
                            totalCapturas = 0, // TODO: conectar con capturas reales
                            onClick       = { onMateriaClick(materia) },
                            onEditar      = onEditarMateria,
                            onEliminar    = onEliminarMateria,
                        )
                    }
                }

                // Botón cámara
                Column(modifier = Modifier.fillMaxWidth()) {
                    BotonCamara(onClick = onCamaraClick)
                }
            }
        }
    }

    // Diálogo agregar / editar
    if (uiState.mostrarDialogo) {
        DialogoMateria(
            titulo            = if (uiState.materiaEnEdicion == null)
                "Agregar Materia" else "Editar Materia",
            nombre            = uiState.formularioNombre,
            profesor          = uiState.formularioProfesor,
            colorSeleccionado = uiState.formularioColor,
            error             = uiState.error,
            onNombreChange    = onNombreChange,
            onProfesorChange  = onProfesorChange,
            onColorChange     = onColorChange,
            onGuardar         = onGuardar,
            onCancelar        = onCancelar,
        )
    }
}


// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun HomeUiScreenPreview() {
    HomeUiScreen(
        uiState = HomeUiState(
            materias = listOf(
                Materia(1, "Cálculo Multivariable", "Prof. Sirgei", "4CAF50"),
                Materia(2, "Cálculo Integral",      "Prof. Sirgei", "2196F3"),
                Materia(3, "Cálculo Vectorial",     "Prof. Sirgei", "9C27B0"),
            ),
        ),
        onAgregarMateriaClick = {},
        onMateriaClick        = {},
        onEditarMateria       = {},
        onEliminarMateria     = {},
        onNombreChange        = {},
        onProfesorChange      = {},
        onColorChange         = {},
        onGuardar             = {},
        onCancelar            = {},
        onCamaraClick         = {},
        onErrorDismissed      = {},
    )
}