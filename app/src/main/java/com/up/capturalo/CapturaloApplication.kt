package com.up.capturalo

import android.app.Application
import com.up.capturalo.di.AppModule

/**
 * Application personalizada que aloja el [AppModule] como singleton global.
 * Recuerda registrarla en AndroidManifest.xml:
 *   android:name=".CapturaloApplication"
 */
class CapturaloApplication : Application() {

    /** Punto de acceso único al contenedor de dependencias. */
    lateinit var appModule: AppModule
        private set

    override fun onCreate() {
        super.onCreate()
        appModule = AppModule()
    }
}
