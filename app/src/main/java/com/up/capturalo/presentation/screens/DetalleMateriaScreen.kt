package com.up.capturalo.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.up.capturalo.data.models.Captura
import com.up.capturalo.data.models.Materia
import com.up.capturalo.presentation.components.BotonCamara
import com.up.capturalo.presentation.components.BotonCancelar
import com.up.capturalo.presentation.components.CapturaItem

@Composable
fun DetalleMateriaScreen(
    materia: Materia,
    capturas: List<Captura>,
    onCapturaClick: (Captura) -> Unit,
    onTomarFotoClick: () -> Unit = {},
    onSalirClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Column principal que envuelve toda la estructura vertical de la pantalla.
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── 1. Header Azul Superior ──────────────────────────────────────────
        // Card contenedor con el color azul (#0066FF) institucional solicitado.
        // Presenta el nombre de la materia, el profesor y la cantidad actual de capturas.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0066FF)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Título: Nombre de la materia
                Text(
                    text = materia.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                // Subtítulo: Nombre del profesor
                Text(
                    text = materia.profesor,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )

                // Contador de capturas disponibles
                Text(
                    text = "Capturas: ${capturas.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // ── 2. Zona Central: Galería en Grilla ────────────────────────────────
        // Grid de 2 columnas fijas.
        // El modificador `.weight(1f)` es fundamental: absorbe todo el espacio vertical disponible
        // entre el Header superior y el Botón inferior, permitiendo scroll interno en la grilla.
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Expande la grilla en el espacio restante de la columna.
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Renderiza cada ítem de captura perezosamente.
            items(
                items = capturas,
                key = { it.id } // Llave única para optimizar recomposiciones y animaciones.
            ) { captura ->
                CapturaItem(
                    captura = captura,
                    onClick = { onCapturaClick(captura) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ── 3. Botón de Cámara para esta materia ──────────────────────────────
        BotonCamara(
            onClick = onTomarFotoClick
        )

        // ── 4. Botón Inferior: Salir ──────────────────────────────────────────
        // Botón rojo (#FF0000) de ancho completo para salir o regresar de la pantalla.
        BotonCancelar(
            texto = "Salir",
            onClick = onSalirClick
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews para Android Studio
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
        Captura(id = 3, idMateria = 1, rutaImagen = "img3.jpg"),
        Captura(id = 4, idMateria = 1, rutaImagen = "img4.jpg"),
        Captura(id = 5, idMateria = 1, rutaImagen = "img5.jpg"),
        Captura(id = 6, idMateria = 1, rutaImagen = "img6.jpg")
    )

    DetalleMateriaScreen(
        materia = materiaMock,
        capturas = capturasMock,
        onCapturaClick = {},
        onSalirClick = {}
    )
}
