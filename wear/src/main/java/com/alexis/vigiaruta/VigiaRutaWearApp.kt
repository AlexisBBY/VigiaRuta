package com.alexis.vigiaruta

import android.app.Application
import com.alexis.vigiaruta.di.ServiceLocator

class VigiaRutaWearApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
