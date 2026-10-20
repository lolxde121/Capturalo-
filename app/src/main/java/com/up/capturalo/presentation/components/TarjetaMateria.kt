package com.up.capturalo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.up.capturalo.data.models.Materia
import com.up.capturalo.utils.toComposeColor

/**
 * Tarjeta que representa una [Materia].
 *
 * Completamente stateless: recibe datos y emite eventos mediante lambdas.
 *
 * @param materia        Datos de la materia a mostrar.
 * @param totalCapturas  Cantidad de capturas asociadas.
 * @param onClick        Evento al pulsar la tarjeta (navegar al detalle).
 * @param onEditar       Evento al pulsar el ícono de editar.
 * @param onEliminar     Evento al pulsar el ícono de eliminar.
 * @param parseoColor    Función de parseo de hex → Color (inyectable para tests).
 */
@Composable
fun TarjetaMateria(
    materia: Materia,
    totalCapturas: Int,
    onClick: () -> Unit,
    onEditar: (Materia) -> Unit = {},
    onEliminar: (Int) -> Unit = {},
    parseoColor: (String) -> Color = { it.toComposeColor() },
) {
    Card(
        modifier  = Modifier
            .width(225.dp)
            .height(200.dp)
            .padding(8.dp),
        onClick   = onClick,
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column {
            // Franja de color de la materia
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(parseoColor(materia.color))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Nombre y profesor
                Text(materia.nombre,   style = MaterialTheme.typography.titleLarge)
                Text(materia.profesor, style = MaterialTheme.typography.titleMedium)

                // Badge de capturas
                PildoraGris(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(110.dp)
                        .height(20.dp),
                    texto = "Capturas: $totalCapturas",
                )

                // Botones de acción: editar / eliminar
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment     = Alignment.Bottom,
                ) {
                    IconButton(onClick = { onEditar(materia) }) {
                        Icon(
                            imageVector        = Icons.Default.Edit,
                            contentDescription = "Editar materia",
                            tint               = Color.DarkGray,
                        )
                    }
                    IconButton(onClick = { onEliminar(materia.id) }) {
                        Icon(
                            imageVector        = Icons.Default.Delete,
                            contentDescription = "Eliminar materia",
                            tint               = Color.Red,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TarjetaMateriaPreview() {
    TarjetaMateria(
        materia       = Materia(1, "Cálculo Multivariable", "Prof. Sirgei", "4CAF50"),
        totalCapturas = 5,
        onClick       = {},
        onEditar      = {},
        onEliminar    = {},
    )
}
