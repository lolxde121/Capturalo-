package com.up.capturalo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.up.capturalo.utils.toComposeColor

@Composable
fun ColorSelector(
    coloresDisponibles: List<String>,
    colorSeleccionado: String,
    onColorSeleccionado: (String) -> Unit,
    parseoColor: (String) -> Color = { it.toComposeColor() }
){
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ){
        coloresDisponibles.forEach {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(parseoColor(it))
                    .clickable {
                        onColorSeleccionado(it)
                    }

            )
        }

    }


}

@Preview
@Composable
fun ColorSelectorPreview(){
    ColorSelector(
        listOf("#00FF00", "#A020F0", "#FF8000", "#FF0000"),
        "#00FF00",
        {}
    )
}

