package com.example.flores

import android.app.Application
import com.example.flores.data.AppContainer
import com.example.flores.data.ItemRepository

class FloresApplication : Application() {

    // Inicialización segura y diferida del repositorio local
    val localRepository: ItemRepository by lazy {
        AppContainer.getLocalRepository(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}