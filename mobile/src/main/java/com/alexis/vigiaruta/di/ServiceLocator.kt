package com.alexis.vigiaruta.di

import android.content.Context
import com.alexis.vigiaruta.data.local.AppDatabase
import com.alexis.vigiaruta.data.repository.EventoRepository
import com.alexis.vigiaruta.data.ubicacion.ProveedorUbicacion

object ServiceLocator {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val eventoRepository: EventoRepository by lazy {
        EventoRepository(
            dao = AppDatabase.obtener(appContext).eventoDao(),
            ubicacion = ProveedorUbicacion(appContext)
        )
    }
}
