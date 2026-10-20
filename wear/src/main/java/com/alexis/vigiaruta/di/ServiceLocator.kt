package com.alexis.vigiaruta.di

import android.content.Context
import com.alexis.vigiaruta.data.AlertadorAndroid
import com.alexis.vigiaruta.data.MonitoreoRepositorio
import com.alexis.vigiaruta.data.comunicacion.EnviadorEventos
import com.alexis.vigiaruta.data.sensor.FuenteSensores
import com.alexis.vigiaruta.data.sensor.SensoresAndroid
import com.alexis.vigiaruta.data.sensor.SensoresSimulados
import com.alexis.vigiaruta.domain.DetectorFatiga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object ServiceLocator {

    private lateinit var appContext: Context
    private val scopeApp = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    var modoSimulacion: Boolean = true

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val enviador: EnviadorEventos by lazy { EnviadorEventos(appContext) }

    val monitoreoRepositorio: MonitoreoRepositorio by lazy {
        MonitoreoRepositorio(
            alertador = AlertadorAndroid(appContext),
            enviador = enviador,
            scope = scopeApp
        )
    }

    fun fuenteSensores(): FuenteSensores =
        if (modoSimulacion) SensoresSimulados() else SensoresAndroid(appContext)

    fun crearDetector(): DetectorFatiga = DetectorFatiga(
        duracionCalibracionMs = if (modoSimulacion) {
            DetectorFatiga.CALIBRACION_SIMULADA_MS
        } else {
            DetectorFatiga.CALIBRACION_REAL_MS
        }
    )
}
