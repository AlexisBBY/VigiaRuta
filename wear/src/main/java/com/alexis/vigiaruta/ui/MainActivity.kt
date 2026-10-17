package com.alexis.vigiaruta.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexis.vigiaruta.service.MonitoreoService

class MainActivity : ComponentActivity() {

    private val viewModel: MonitoreoViewModel by viewModels { MonitoreoViewModel.Factory }

    private val pedirPermisos = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        pedirPermisos.launch(
            arrayOf(Manifest.permission.BODY_SENSORS, Manifest.permission.POST_NOTIFICATIONS)
        )

        setContent {
            val estado by viewModel.estado.collectAsStateWithLifecycle()
            val modoSimulacion by viewModel.modoSimulacion.collectAsStateWithLifecycle()

            VigiaRutaWear(
                estado = estado,
                modoSimulacion = modoSimulacion,
                onIniciar = { MonitoreoService.iniciar(this) },
                onDetener = { MonitoreoService.detener(this) },
                onCambiarModo = viewModel::cambiarModoSimulacion,
                onCirculoTocado = viewModel::onCirculoTocado
            )
        }
    }
}
