package com.up.capturalo.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.up.capturalo.R

/**
 * Botón stateless que notifica al padre cuando el usuario quiere agregar una materia.
 *
 * @param onClick Lambda emitida al pulsar el botón.
 */
@Composable
fun BotonAgregarMateria(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier
            .width(175.dp)
            .height(50.dp),
        shape  = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        ),
        onClick = onClick,
    ) {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text  = stringResource(R.string.btn_agregar_materia),
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Preview
@Composable
fun BotonAgMateriaPreview() {
    BotonAgregarMateria()
}
