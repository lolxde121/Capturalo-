package com.up.capturalo.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.up.capturalo.R
import com.up.capturalo.presentation.components.ColorSelector
import com.up.capturalo.presentation.components.TextFieldFormulario
import com.up.capturalo.presentation.viewmodel.coloresDisponibles

/**
 * Pantalla de formulario 100% stateless para registrar una nueva materia.
 *
 * Aplica State Hoisting: Recibe datos inmutables y notifica cambios mediante lambdas.
 * Libre de textos fijos usando [stringResource].
 */
@Composable
fun AgregarMateriaScreen(
    nombre: String,
    profesor: String,
    colorSeleccionado: String,
    onNombreChange: (String) -> Unit,
    onProfesorChange: (String) -> Unit,
    onColorClick: (String) -> Unit,
    onGuardarClick: () -> Unit,
    onCancelarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Banner superior temático
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.header_nueva_materia),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                    )
                }
            }

            // Tarjeta de campos de formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFBDBDBD)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    TextFieldFormulario(
                        valor = nombre,
                        onValueChange = onNombreChange,
                        label = stringResource(R.string.label_nombre_materia),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    TextFieldFormulario(
                        valor = profesor,
                        onValueChange = onProfesorChange,
                        label = stringResource(R.string.label_nombre_profesor),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Text(
                        text = stringResource(R.string.label_color_materia),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                    )

                    ColorSelector(
                        coloresDisponibles = coloresDisponibles,
                        colorSeleccionado = colorSeleccionado,
                        onColorSeleccionado = onColorClick,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onGuardarClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.btn_guardar),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }

        // Botón inferior para cancelar
        Button(
            onClick = onCancelarClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .height(64.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
            ),
        ) {
            Text(
                text = stringResource(R.string.btn_cancelar),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AgregarMateriaScreenPreview() {
    AgregarMateriaScreen(
        nombre = "Cálculo Integral",
        profesor = "Prof. García",
        colorSeleccionado = "4CAF50",
        onNombreChange = {},
        onProfesorChange = {},
        onColorClick = {},
        onGuardarClick = {},
        onCancelarClick = {},
    )
}
