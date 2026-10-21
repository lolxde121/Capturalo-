package com.up.capturalo.di

import android.content.Context
import com.up.capturalo.data.dataSource.CapturaloDataSource
import com.up.capturalo.data.repository.CapturaloRepository
import com.up.capturalo.data.repository.CapturaloRepositoryImp

/**
 * Contenedor manual de dependencias (patrón AppContainer / Service Locator).
 *
 * Se inicializa UNA sola vez en [com.up.capturalo.CapturaloApplication] pasándole
 * el contexto global de la aplicación (`applicationContext`).
 *
 * @param context Permite a [CapturaloDataSource] acceder al almacenamiento interno de Android.
 */
class AppModule(private val context: Context) {

    // Singleton del DataSource que gestiona la persistencia con DataStore + Gson
    val dataSource: CapturaloDataSource by lazy {
        CapturaloDataSource(context)
    }

    // Singleton del Repository expuesto exclusivamente a través de su interfaz
    val repository: CapturaloRepository by lazy {
        CapturaloRepositoryImp(dataSource)
    }
}