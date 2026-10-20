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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.up.capturalo.presentation.components.ColorSelector
import com.up.capturalo.presentation.components.TextFieldFormulario
import com.up.capturalo.presentation.viewmodel.coloresDisponibles

// ─────────────────────────────────────────────────────────────────────────────
//  AgregarMateriaScreen — Pantalla 100 % STATELESS
//
//  ¿Por qué stateless?
//  En Compose el patrón recomendado es "State Hoisting": la pantalla no guarda
//  ningún valor propio; recibe los valores actuales desde el ViewModel (a través
//  del Composable padre) y notifica cambios con lambdas. Así la pantalla es
//  predecible, testeable y reutilizable sin efectos secundarios.
//
//  ¿Por qué NO usamos `remember` ni `LaunchedEffect` aquí?
//  - `remember` almacena estado local → viola el principio stateless.
//  - `LaunchedEffect` lanza corrutinas → lógica de negocio que debe vivir
//    en el ViewModel, no en la UI.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Pantalla de formulario para agregar una nueva materia.
 *
 * @param nombre              Valor actual del campo "Nombre de la materia".
 *                            Viene del ViewModel y se muestra tal cual.
 *
 * @param profesor            Valor actual del campo "Nombre del profesor".
 *                            Igual que [nombre]: lo controla el ViewModel.
 *
 * @param colorSeleccionado   Código hexadecimal (sin "#") del color activo,
 *                            p.ej. "4CAF50". El [ColorSelector] lo usa para
 *                            resaltar visualmente cuál está marcado.
 *
 * @param onNombreChange      Lambda que se ejecuta cada vez que el usuario
 *                            escribe en el campo de nombre.
 *                            Sintaxis: `(nuevoTexto: String) -> Unit`
 *                            El parámetro String es el texto completo
 *                            después del cambio (no solo el caracter nuevo).
 *
 * @param onProfesorChange    Igual que [onNombreChange] pero para el campo
 *                            de profesor.
 *
 * @param onColorClick        Lambda que recibe el hex del color pulsado.
 *                            El ViewModel actualizará [colorSeleccionado]
 *                            y la UI se recompone automáticamente.
 *
 * @param onGuardarClick      Lambda sin parámetros: se dispara cuando el
 *                            usuario pulsa "Guardar". El ViewModel valida
 *                            y persiste la materia.
 *
 * @param onCancelarClick     Lambda sin parámetros: se dispara al pulsar
 *                            "Cancelar". Normalmente el ViewModel limpia el
 *                            formulario y navega hacia atrás.
 *
 * @param modifier            Modificador opcional para que el Composable
 *                            padre controle el tamaño/posición de la pantalla.
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
    // ── Columna raíz ──────────────────────────────────────────────────────────
    // `Arrangement.SpaceBetween` distribuye el espacio disponible de modo que
    // el primer hijo (banner + tarjeta) quede arriba y el último hijo
    // (botón Cancelar) quede pegado al fondo. Es la forma idiomática de Compose
    // para lograr el efecto "footer fijo sin Scaffold".
    Column(
        modifier = modifier
            .fillMaxSize()          // Ocupa toda la pantalla disponible.
            .padding(12.dp),        // Margen exterior uniforme de 12 dp.
        verticalArrangement = Arrangement.SpaceBetween, // ← clave del layout
    ) {

        // ── Bloque superior (banner + formulario) ─────────────────────────────
        // Agrupamos banner y tarjeta en una Column anidada para que
        // SpaceBetween los trate como un único bloque "arriba".
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp), // espacio entre hijos
        ) {

            // ── 1. Banner azul ────────────────────────────────────────────────
            // Reutilizamos el mismo Card de TopBarBlue para mantener consistencia
            // visual, pero lo construimos directamente aquí para controlar el texto.
            // `shape = RoundedCornerShape(12.dp)` recorta las 4 esquinas del Card
            // dándoles un radio de 12 dp — cuanto mayor el valor, más redondas.
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = RoundedCornerShape(12.dp), // esquinas redondeadas
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1565C0), // Azul oscuro institucional
                ),
            ) {
                // Centramos el texto verticalmente dentro del Card.
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Nueva Materia",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                    )
                }
            }

            // ── 2. Tarjeta blanca de formulario ───────────────────────────────
            // `border` dibuja un borde gris alrededor de la tarjeta para
            // dar profundidad sin sombra excesiva (estilo "outlined card").
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White, // fondo blanco
                ),
                // El borde gris aclara visualmente que es un contenedor
                // distinto del fondo de la pantalla.
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFBDBDBD), // gris claro Material
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),           // padding interior del formulario
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {

                    // ── Campo: Nombre de la materia ───────────────────────────
                    // TextFieldFormulario es nuestro componente propio (stateless).
                    // Le pasamos `valor` = estado actual, `onValueChange` = lambda
                    // que sube el nuevo texto al ViewModel.
                    TextFieldFormulario(
                        valor = nombre,
                        onValueChange = onNombreChange, // ← lambda que sube el cambio
                        label = "Nombre de la materia",
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // ── Campo: Nombre del profesor ────────────────────────────
                    TextFieldFormulario(
                        valor = profesor,
                        onValueChange = onProfesorChange,
                        label = "Nombre del profesor",
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // ── Selector de color ─────────────────────────────────────
                    // Etiqueta descriptiva antes del selector.
                    Text(
                        text = "Color de la materia:",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                    )

                    // ColorSelector dibuja una fila de círculos de colores.
                    // `coloresDisponibles` viene del ViewModel (lista de hexes).
                    // `colorSeleccionado` marca cuál está activo (visual).
                    // `onColorSeleccionado` sube el hex pulsado al ViewModel.
                    ColorSelector(
                        coloresDisponibles = coloresDisponibles,
                        colorSeleccionado = colorSeleccionado,
                        onColorSeleccionado = onColorClick, // ← lambda del padre
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ── Botón "Guardar" ───────────────────────────────────────
                    // Botón verde de ancho completo dentro de la tarjeta.
                    // `onClick = onGuardarClick` conecta el clic con el ViewModel
                    // a través de la lambda recibida como parámetro (NO hay lógica
                    // de validación aquí — esa responsabilidad es del ViewModel).
                    Button(
                        onClick = onGuardarClick, // ← lambda sin parámetros
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50), // verde Material
                        ),
                    ) {
                        Text(
                            text = "Guardar",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        } // fin bloque superior

        // ── 3. Botón "Cancelar" (footer) ──────────────────────────────────────
        // SpaceBetween empuja este botón hasta el fondo de la Column raíz.
        // Reutilizamos BotonCancelar, pero necesitamos pasarle el onClick.
        // Como BotonCancelar actual no expone onClick, lo envolvemos en un
        // Column con clickable; alternativamente, si se actualiza el componente,
        // se puede pasar directamente.
        //
        // NOTA EDUCATIVA: aquí sí usamos el componente existente BotonCancelar.
        // Observa que su `onClick` interno es `{}` (vacío). Para conectar la
        // lambda real usaríamos un parámetro; por eso lo ideal es actualizar
        // BotonCancelar para aceptar `onClick: () -> Unit`. Por ahora lo
        // replicamos directamente con la misma apariencia para ser funcionales.
        Button(
            onClick = onCancelarClick, // ← lambda que viene del padre
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .height(72.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F), // rojo Material 700
            ),
        ) {
            Text(
                text = "Cancelar",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Preview
//
//  Las @Preview son *solo para Android Studio* — no afectan el build final.
//  Pasamos valores ficticios para simular el estado que vendría del ViewModel.
//  `showBackground = true` dibuja un fondo blanco para que la tarjeta se vea.
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AgregarMateriaScreenPreview() {
    AgregarMateriaScreen(
        nombre = "Cálculo Integral",
        profesor = "Prof. García",
        colorSeleccionado = "4CAF50", // verde seleccionado por defecto
        onNombreChange = {},           // lambda vacía → no hace nada en preview
        onProfesorChange = {},
        onColorClick = {},
        onGuardarClick = {},
        onCancelarClick = {},
    )
}
