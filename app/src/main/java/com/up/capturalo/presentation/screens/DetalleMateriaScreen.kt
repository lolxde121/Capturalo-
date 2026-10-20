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
import com.up.capturalo.presentation.components.BotonCancelar
import com.up.capturalo.presentation.components.CapturaItem

/**
 * Pantalla de detalle de una materia (100% Stateless).
 *
 * CONCEPTOS CLAVE DE ARQUITECTURA:
 * 1. UI Stateless y State Hoisting:
 *    - La pantalla no mantiene ningún estado propio (`remember`, mutaciones, etc.) ni conoce ViewModels.
 *    - Recibe el estado desde fuera ([materia], [capturas]) y delega cualquier interacción
 *      hacia el exterior mediante funciones lambda ([onCapturaClick], [onSalirClick]).
 *    - Esto hace que el componente sea altamente reutilizable, predecible y fácil de probar/previsualizar.
 *
 * 2. Manejo de eventos con Lambdas:
 *    - `onCapturaClick: (Captura) -> Unit`: Comunica al invocador cuál captura fue presionada pasando
 *      el modelo como argumento, permitiendo navegar o abrir un visor.
 *    - `onSalirClick: () -> Unit`: Comunica la intención de volver atrás o cerrar la vista.
 *
 * 3. LazyVerticalGrid y weight(1f):
 *    - `LazyVerticalGrid`: A diferencia de una columna o fila regular, una lista/grilla "Lazy" (perezosa)
 *      solo compone y renderiza los elementos visibles en pantalla. Esto garantiza un rendimiento
 *      óptimo y un consumo de memoria mínimo al manejar colecciones grandes de imágenes.
 *    - `Modifier.weight(1f)` dentro de un `Column`: Indica al sistema de diseño que este componente
 *      debe expandirse para ocupar todo el espacio vertical sobrante después de medir el Header
 *      y el Botón inferior. De este modo, la grilla aprovecha todo el centro de la pantalla y
 *      el botón de "Salir" queda fijado en la parte inferior sin salir del límite de la pantalla.
 */
@Composable
fun DetalleMateriaScreen(
    materia: Materia,
    capturas: List<Captura>,
    onCapturaClick: (Captura) -> Unit,
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

        // ── 3. Botón Inferior: Salir ──────────────────────────────────────────
        // Botón rojo (#FF0000) de ancho completo para salir o regresar de la pantalla.
        BotonCancelar("Salir")
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
