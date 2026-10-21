package com.up.capturalo.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.up.capturalo.data.models.Materia
import com.up.capturalo.presentation.components.BotonCancelar
import com.up.capturalo.presentation.components.TarjetaMateria
import com.up.capturalo.presentation.components.TopBarBlue

/**
 * Pantalla que permite al usuario seleccionar a qué materia asignar la nueva captura fotográfica.
 *
 * @param materias Lista de materias activas disponibles.
 * @param totalCapturasProvider Función que retorna el conteo de capturas de una materia por su ID.
 * @param onMateriaClick Lambda que recibe la materia seleccionada para disparar la cámara.
 * @param onCancelarClick Lambda para cancelar el flujo y regresar a la pantalla principal.
 */
@Composable
fun TomarFotoUiScreen(
    materias: List<Materia>,
    totalCapturasProvider: (Int) -> Int = { 0 },
    onMateriaClick: (Materia) -> Unit,
    onCancelarClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) { TopBarBlue("Selecciona la Ubicación") }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Cuadrícula de materias disponibles como destino
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f)
            ) {
                items(materias.size) { index ->
                    val materia = materias[index]
                    TarjetaMateria(
                        materia = materia,
                        totalCapturas = totalCapturasProvider(materia.id),
                        onClick = { onMateriaClick(materia) }
                    )
                }
            }
            BotonCancelar(
                texto = "Cancelar",
                onClick = onCancelarClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TomarFotoUiScreenPreview() {
    TomarFotoUiScreen(
        materias = listOf(
            Materia(1, "Cálculo Multivariable", "Prof. Sirgei", "4CAF50"),
            Materia(2, "Cálculo Integral", "Prof. Sirgei", "2196F3"),
            Materia(3, "Cálculo Vectorial", "Prof. Sirgei", "9C27B0")
        ),
        totalCapturasProvider = { 3 },
        onMateriaClick = {},
        onCancelarClick = {}
    )
}