package com.up.capturalo.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.up.capturalo.R
import com.up.capturalo.data.models.Captura
@Composable
fun CapturaItem(
    captura: Captura,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ){
        Image(
            painter = painterResource(id = R.drawable.ejmploimg),
            contentDescription = "Imagen de la captura",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(200.dp)
        )
    }
}

@Preview
@Composable
fun CapturaItemPreview(){
    CapturaItem(Captura(1, 1, "", System.currentTimeMillis()), {})
}
