package com.alexis.vigiaruta.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexis.vigiaruta.data.local.EventoEntity

class MainActivity : ComponentActivity() {

    private val viewModel: HistorialViewModel by viewModels { HistorialViewModel.Factory }

    private val pedirPermisos = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pedirPermisos.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        )

        setContent {
            MaterialTheme {
                val estado by viewModel.estado.collectAsStateWithLifecycle()
                HistorialScreen(estado = estado, onVerEnMapa = ::abrirMapa)
            }
        }
    }

    private fun abrirMapa(evento: EventoEntity) {
        val uri = Uri.parse("geo:${evento.latitud},${evento.longitud}?q=${evento.latitud},${evento.longitud}(Alerta)")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}
