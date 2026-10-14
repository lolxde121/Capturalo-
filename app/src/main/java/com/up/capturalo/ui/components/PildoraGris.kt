package com.up.capturalo.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PildoraGris(modifier: Modifier = Modifier, texto : String){
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color.Gray
    ) {
        Text(
            modifier = Modifier.padding(2.dp),
            text = texto,
            fontSize = 12.sp,
            textAlign = TextAlign.Start
        )

    }
}