package com.alexis.vigiaruta

import android.app.Application
import com.alexis.vigiaruta.di.ServiceLocator
import com.alexis.vigiaruta.service.Notificador

class VigiaRutaMobileApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
        Notificador.crearCanal(this)
    }
}
