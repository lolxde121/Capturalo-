package com.up.capturalo.di

import com.up.capturalo.data.dataSource.CapturaloDataSource
import com.up.capturalo.data.repository.CapturaloRepository
import com.up.capturalo.data.repository.CapturaloRepositoryImp

/**
 * Contenedor manual de dependencias (patrón Service Locator / AppContainer).
 * Se instancia UNA sola vez en [com.up.capturalo.CapturaloApplication] y
 * se accede desde [MainActivity] para construir el ViewModel.
 *
 * No se usa Hilt ni Koin para cumplir con la restricción de DI manual.
 */
class AppModule {

    // Singleton del DataSource: única fuente de verdad en RAM
    val dataSource: CapturaloDataSource by lazy {
        CapturaloDataSource()
    }

    // Singleton del Repository expuesto por su interfaz
    val repository: CapturaloRepository by lazy {
        CapturaloRepositoryImp(dataSource)
    }
}