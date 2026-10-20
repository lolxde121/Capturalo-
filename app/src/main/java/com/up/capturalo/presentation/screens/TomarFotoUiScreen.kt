package com.up.capturalo.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.up.capturalo.data.models.Materia
import com.up.capturalo.presentation.components.BotonCancelar
import com.up.capturalo.presentation.components.TarjetaMateria
import com.up.capturalo.presentation.components.TopBarBlue


@Composable
fun TomarFotoUiScreen(
    onMateriaClick: (Any?) -> Unit,
    materias: List<Materia>
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) { TopBarBlue("Selecciona la Ubicacion") }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)

        ) {
            // materias a donde poner la foto
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
            ) {
                items(materias.size){ index ->
                    val materia = materias[index]
                    TarjetaMateria(
                        materia = materia,
                        totalCapturas = 5,
                        onClick = { onMateriaClick(materia) }
                    )
                }
            }
            BotonCancelar("Cancelar")
        }

    }
}
@Preview
@Composable
fun TomarFotoUiScreenPreview() {

    TomarFotoUiScreen(
        onMateriaClick = {},
        materias = mutableListOf(
        Materia(1, "Calculo Multivariable", "Prof. Sirgei", "00FF00")
        ,
        Materia(2, "Calculo Integral", "Prof. Sirgei", "00FF00")
        ,
        Materia(3, "Calculo Vectorial", "Prof. Sirgei", "00FF00")
    ))
}