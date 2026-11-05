package com.up.capturalo.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.up.capturalo.R
import com.up.capturalo.presentation.viewmodel.coloresDisponibles

/**
 * Diálogo 100% stateless para crear o editar una [com.up.capturalo.data.models.Materia].
 *
 * @param titulo            Texto del encabezado ("Agregar Materia" / "Editar Materia").
 * @param nombre            Valor actual del campo nombre.
 * @param profesor          Valor actual del campo profesor.
 * @param colorSeleccionado Hex del color activo (sin #).
 * @param error             Mensaje de error; si es null no se muestra.
 * @param onNombreChange    Lambda al cambiar el campo nombre.
 * @param onProfesorChange  Lambda al cambiar el campo profesor.
 * @param onColorChange     Lambda al seleccionar un color.
 * @param onGuardar         Lambda al confirmar (Create o Update).
 * @param onCancelar        Lambda al descartar el diálogo.
 */
@Composable
fun DialogoMateria(
    titulo: String,
    nombre: String,
    profesor: String,
    colorSeleccionado: String,
    error: String?,
    onNombreChange: (String) -> Unit,
    onProfesorChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
) {
    Dialog(onDismissRequest = onCancelar) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título
                Text(
                    text  = titulo,
                    style = MaterialTheme.typography.titleLarge,
                )

                // Campo nombre
                TextFieldFormulario(
                    valor         = nombre,
                    onValueChange = onNombreChange,
                    label         = stringResource(R.string.label_nombre_materia),
                    modifier      = Modifier.fillMaxWidth(),
                )

                // Campo profesor
                TextFieldFormulario(
                    valor         = profesor,
                    onValueChange = onProfesorChange,
                    label         = stringResource(R.string.label_nombre_profesor),
                    modifier      = Modifier.fillMaxWidth(),
                )

                // Selector de color
                Text(
                    text  = stringResource(R.string.label_color_materia),
                    style = MaterialTheme.typography.labelLarge,
                )
                ColorSelector(
                    coloresDisponibles   = coloresDisponibles,
                    colorSeleccionado    = colorSeleccionado,
                    onColorSeleccionado  = onColorChange,
                )

                // Mensaje de error
                if (error != null) {
                    Text(
                        text  = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Botones
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TextButton(
                        onClick  = onCancelar,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.btn_cancelar), color = Color.Gray)
                    }

                    Button(
                        onClick  = onGuardar,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(8.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                    ) {
                        Text(stringResource(R.string.btn_guardar), color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DialogoMateriaAgregarPreview() {
    DialogoMateria(
        titulo             = "Agregar Materia",
        nombre             = "",
        profesor           = "",
        colorSeleccionado  = "4CAF50",
        error              = null,
        onNombreChange     = {},
        onProfesorChange   = {},
        onColorChange      = {},
        onGuardar          = {},
        onCancelar         = {},
    )
}
