package com.up.capturalo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.up.capturalo.data.dataSeed
import com.up.capturalo.models.Materia
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
        onClick = onClick
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(parseoColor(materia.color))
            )
            Text(materia.nombre, style = MaterialTheme.typography.titleLarge)
            Text(materia.profesor, style = MaterialTheme.typography.titleMedium)
            Surface(
                modifier = Modifier
                .width(100.dp)
                .height(20.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color.Gray
            ) {
                Column(modifier = Modifier
                    .fillMaxSize().padding(25.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(2.dp),
                        text = "Capturas: $totalCapturas",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                }

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
