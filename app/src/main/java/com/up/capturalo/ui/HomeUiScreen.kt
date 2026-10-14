package com.up.capturalo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.up.capturalo.viewmodel.HomeUIviewModel


@Composable
fun HomeUiScreen(viewModel: HomeUIviewModel = viewModel()){

}

@Preview
@Composable
fun HomeUiScreenPreview(){
    HomeUiScreen()
}