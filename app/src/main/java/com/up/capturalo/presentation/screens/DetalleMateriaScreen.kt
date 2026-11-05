package com.up.capturalo.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.up.capturalo.R
import com.up.capturalo.data.models.Captura
import com.up.capturalo.data.models.Materia
import com.up.capturalo.presentation.components.BotonCamara
import com.up.capturalo.presentation.components.BotonCancelar
import com.up.capturalo.presentation.components.CapturaItem

/**
 * Pantalla 100% stateless para visualizar el detalle y las capturas de una materia.
 */
@Composable
fun DetalleMateriaScreen(
    materia: Materia,
    capturas: List<Captura>,
    onCapturaClick: (Captura) -> Unit,
    onTomarFotoClick: () -> Unit = {},
    onSalirClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── 1. Header Superior Tematizado ──────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = materia.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = materia.profesor,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Text(
                    text = stringResource(R.string.capturas_count_format, capturas.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // ── 2. Zona Central: Galería en Grilla o Estado Vacío ──────────────────
        if (capturas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no hay capturas para esta materia.\n¡Usa el botón de abajo para tomar una foto!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = capturas,
                    key = { it.id }
                ) { captura ->
                    CapturaItem(
                        captura = captura,
                        onClick = { onCapturaClick(captura) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // ── 3. Botón de Cámara para esta materia ──────────────────────────────
        BotonCamara(
            onClick = onTomarFotoClick
        )

        // ── 4. Botón Inferior: Salir ──────────────────────────────────────────
        BotonCancelar(
            texto = stringResource(R.string.btn_salir),
            onClick = onSalirClick
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetalleMateriaScreenPreview() {
    val materiaMock = Materia(
        id = 1,
        nombre = "Cálculo Multivariable",
        profesor = "Prof. Sirgei",
        color = "2196F3"
    )

    val capturasMock = listOf(
        Captura(id = 1, idMateria = 1, rutaImagen = "img1.jpg"),
        Captura(id = 2, idMateria = 1, rutaImagen = "img2.jpg"),
    )

    DetalleMateriaScreen(
        materia = materiaMock,
        capturas = capturasMock,
        onCapturaClick = {},
        onSalirClick = {}
    )
}
