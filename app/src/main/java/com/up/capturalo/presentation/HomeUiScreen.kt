package com.up.capturalo.presentation

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.up.capturalo.data.models.Materia
import com.up.capturalo.presentation.components.BotonAgregarMateria
import com.up.capturalo.presentation.components.PildoraGris
import com.up.capturalo.presentation.components.TarjetaMateria
import com.up.capturalo.presentation.components.TopBarBlue
import com.up.capturalo.presentation.viewmodel.HomeUIviewModel


@Composable
fun HomeUiScreenContent(
    viewModel: HomeUIviewModel = viewModel(),
    materias: List<Materia>,
    onMateriaClick: (Materia) -> Unit,
    onAgregarMateriaClick: () -> Unit,
    onCamaraClick: () -> Unit,
    modifier: Modifier = Modifier
) {


    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) { TopBarBlue() }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BotonAgregarMateria()

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    //topBar
                    Text(
                        text = "Mis Materias",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.titleLarge
                    )

                    PildoraGris(
                        modifier = Modifier
                            .width(100.dp),
                        texto = "Activas"
                    )

                    //materias Disponibles

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .weight(1f)
                            .padding(paddingValues)
                    ) {
                        items(materias.size){
                            index ->
                            val materia = materias[index]
                            TarjetaMateria(
                                materia = materia,
                                totalCapturas = 5,
                                onClick = { onMateriaClick(materia) }
                            )
                        }

                    }

                }
            }

        }
    }
}

@Preview
@Composable
fun HomeUiScreenPreview() {
    HomeUiScreenContent(
        materias = mutableListOf(
            Materia(1, "Calculo Multivariable", "Prof. Sirgei", "00FF00")
            ,
            Materia(2, "Calculo Integral", "Prof. Sirgei", "00FF00")
            ,
            Materia(3, "Calculo Vectorial", "Prof. Sirgei", "00FF00")
        ),
        onMateriaClick = {},
        onAgregarMateriaClick = {},
        onCamaraClick = {}
    )

}