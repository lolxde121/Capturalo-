package com.up.capturalo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.up.capturalo.data.dataSeed
import com.up.capturalo.data.models.Materia
import com.up.capturalo.utils.toComposeColor

@Composable
fun TarjetaMateria(
    materia: Materia,
    totalCapturas: Int,
    onClick: () -> Unit,
    parseoColor: (String) -> Color = { it.toComposeColor() }
){
    Card(
        modifier     = Modifier
            .width(225.dp)
            .height(200.dp)
            .padding(8.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(50.dp)
    ) {
        Column {
            // color de la materia
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(parseoColor(materia.color))
            )
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
            ) {
                // info de la materia
                Text(materia.nombre, style = MaterialTheme.typography.titleLarge)
                Text(materia.profesor, style = MaterialTheme.typography.titleMedium)
                // badge para mostrar las capturas
                PildoraGris( Modifier
                    .padding(top = 55.dp)
                    .width(100.dp)
                    .height(20.dp)
                    ,
                    "Capturas: $totalCapturas"
                )
            }

        }
    }
}

@Preview
@Composable
fun TarjetaMateriaPreview(){
    TarjetaMateria(
        materia = dataSeed.materiasIniciales[0],
        totalCapturas = 5,
        onClick = {}
    )
}
