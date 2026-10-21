package com.up.capturalo.presentation.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.up.capturalo.R
import com.up.capturalo.data.models.Captura
import java.io.File

/**
 * Renderiza una captura fotográfica individual en la cuadrícula de Detalle.
 *
 * Si la captura contiene una ruta de archivo local válida ([Captura.rutaImagen]),
 * decodifica el archivo a Bitmap. De lo contrario, muestra la imagen de muestra por defecto.
 */
@Composable
fun CapturaItem(
    captura: Captura,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(captura.rutaImagen) {
        if (captura.rutaImagen.isNotBlank()) {
            val archivo = File(captura.rutaImagen)
            if (archivo.exists()) {
                BitmapFactory.decodeFile(archivo.absolutePath)
            } else null
        } else null
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto capturada",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ejmploimg),
                contentDescription = "Imagen de la captura de ejemplo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun CapturaItemPreview() {
    CapturaItem(Captura(1, 1, "", System.currentTimeMillis()), {})
}
