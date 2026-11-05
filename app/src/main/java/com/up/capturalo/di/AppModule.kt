package com.up.capturalo.di

import android.content.Context
import com.up.capturalo.data.dataSource.CapturaloDataSource
import com.up.capturalo.data.repository.CapturaloRepository
import com.up.capturalo.data.repository.CapturaloRepositoryImp
import com.up.capturalo.data.storage.ImageStorageService
import com.up.capturalo.data.storage.InternalStorageImageService

/**
 * Contenedor manual de inyección de dependencias (patrón AppContainer / Service Locator).
 *
 * Sigue los principios SOLID:
 * - Dependency Inversion Principle (DIP): Expone dependencias a través de sus interfaces
 *   ([CapturaloRepository], [ImageStorageService]).
 * - Single Responsibility Principle (SRP): Su único rol es la creación y resolución de grafos de objetos.
 *
 * @param context Contexto de la aplicación (`applicationContext`) que garantiza ciclo de vida global.
 */
class AppModule(private val context: Context) {

    // Singleton del DataSource que gestiona la persistencia con DataStore + Gson
    val dataSource: CapturaloDataSource by lazy {
        CapturaloDataSource(context)
    }

    // Singleton del servicio de almacenamiento de imágenes en disco interno
    val imageStorageService: ImageStorageService by lazy {
        InternalStorageImageService(context)
    }

    // Singleton del Repository expuesto exclusivamente a través de su interfaz
    val repository: CapturaloRepository by lazy {
        CapturaloRepositoryImp(
            dataSource = dataSource,
            imageStorageService = imageStorageService
        )
    }
}
