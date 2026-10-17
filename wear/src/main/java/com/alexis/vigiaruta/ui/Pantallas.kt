package com.alexis.vigiaruta.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.alexis.vigiaruta.data.EstadoMonitoreo
import kotlin.random.Random

@Composable
fun VigiaRutaWear(
    estado: EstadoMonitoreo,
    modoSimulacion: Boolean,
    onIniciar: () -> Unit,
    onDetener: () -> Unit,
    onCambiarModo: () -> Unit,
    onCirculoTocado: () -> Unit
) {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            when (estado) {
                EstadoMonitoreo.Inactivo -> PantallaInicio(modoSimulacion, onIniciar, onCambiarModo)
                is EstadoMonitoreo.Calibrando -> PantallaCalibrando(estado, onDetener)
                is EstadoMonitoreo.Monitoreando -> PantallaMonitoreo(estado, onDetener)
                is EstadoMonitoreo.Alerta -> PantallaAlerta(estado, onCirculoTocado)
            }
        }
    }
}

@Composable
private fun PantallaInicio(modoSimulacion: Boolean, onIniciar: () -> Unit, onCambiarModo: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("VigiaRuta", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onIniciar) { Text("Iniciar") }
        Spacer(Modifier.height(8.dp))
        Chip(
            onClick = onCambiarModo,
            label = { Text(if (modoSimulacion) "Simulacion: si" else "Simulacion: no") },
            colors = ChipDefaults.secondaryChipColors()
        )
    }
}

@Composable
private fun PantallaCalibrando(estado: EstadoMonitoreo.Calibrando, onDetener: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Calibrando pulso", textAlign = TextAlign.Center)
        Text("${estado.segundosRestantes} s", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Chip(onClick = onDetener, label = { Text("Detener viaje") }, colors = ChipDefaults.secondaryChipColors())
    }
}

@Composable
private fun PantallaMonitoreo(estado: EstadoMonitoreo.Monitoreando, onDetener: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("${estado.pulso} lpm", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Base personal: ${estado.pulsoBase}", fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Chip(onClick = onDetener, label = { Text("Detener viaje") }, colors = ChipDefaults.secondaryChipColors())
    }
}

@Composable
private fun PantallaAlerta(estado: EstadoMonitoreo.Alerta, onCirculoTocado: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val posicion = remember(estado.inicioMs) { Random.nextFloat() to Random.nextFloat() }
        val margen = 50.dp
        val tamano = 48.dp
        val x = margen + (maxWidth - margen * 2 - tamano) * posicion.first
        val y = maxHeight / 2 + (maxHeight / 2 - margen - tamano) * posicion.second

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Despierta", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Toca el circulo", fontSize = 12.sp)
            Text("Nivel ${estado.nivel}", fontSize = 12.sp)
        }

        Box(
            modifier = Modifier
                .offset(x = x, y = y)
                .size(tamano)
                .clip(CircleShape)
                .background(MaterialTheme.colors.primary)
                .clickable(onClick = onCirculoTocado)
        )
    }
}
