package com.up.capturalo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.up.capturalo.data.repository.CapturaloRepository

/**
 * Factory Method que construye [HomeUIviewModel] inyectando [CapturaloRepository].
 *
 * Se usa en [com.up.capturalo.MainActivity] así:
 * ```kotlin
 * val factory = HomeViewModelFactory(appModule.repository)
 * val viewModel: HomeUIviewModel by viewModels { factory }
 * ```
 */
class HomeViewModelFactory(
    private val repository: CapturaloRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeUIviewModel::class.java)) {
            return HomeUIviewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
