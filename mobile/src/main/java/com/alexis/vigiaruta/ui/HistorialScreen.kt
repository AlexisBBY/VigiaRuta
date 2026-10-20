package com.alexis.vigiaruta.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alexis.vigiaruta.data.local.EventoEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    estado: HistorialUiState,
    onVerEnMapa: (EventoEntity) -> Unit
) {
    Scaffold(topBar = { TopAppBar(title = { Text("VigíaRuta") }) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column {
                    Text("Últimos 5 días", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    GraficaAlertas(estado.resumen)
                    Spacer(Modifier.height(8.dp))
                    TablaReaccion(estado.resumen)
                    Spacer(Modifier.height(16.dp))
                    HorasRiesgo(estado.horasRiesgo)
                }
            }
            item {
                Text("Eventos", style = MaterialTheme.typography.titleMedium)
            }
            if (estado.eventos.isEmpty()) {
                item { Text("Todavía no hay alertas registradas.") }
            }
            items(estado.eventos, key = { it.id }) { evento ->
                TarjetaEvento(evento, onVerEnMapa)
            }
        }
    }
}

@Composable
private fun GraficaAlertas(resumen: List<ResumenDia>) {
    val color = MaterialTheme.colorScheme.primary
    val maximo = (resumen.maxOfOrNull { it.alertas } ?: 0).coerceAtLeast(1)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        if (resumen.isEmpty()) return@Canvas
        val anchoColumna = size.width / resumen.size
        val anchoBarra = anchoColumna * 0.5f
        resumen.forEachIndexed { i, dia ->
            val alto = size.height * dia.alertas / maximo
            drawRect(
                color = color,
                topLeft = Offset(i * anchoColumna + (anchoColumna - anchoBarra) / 2, size.height - alto),
                size = Size(anchoBarra, alto)
            )
        }
    }
    Row(Modifier.fillMaxWidth()) {
        resumen.forEach { dia ->
            Text(
                text = "${dia.etiqueta}\n${dia.alertas}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun TablaReaccion(resumen: List<ResumenDia>) {
    Text("Reacción promedio (s)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    Row(Modifier.fillMaxWidth()) {
        resumen.forEach { dia ->
            Text(
                text = dia.reaccionPromedioSeg?.let { "%.1f".format(it) } ?: "-",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HorasRiesgo(conteos: List<Int>) {
    val color = MaterialTheme.colorScheme.error
    val maximo = conteos.maxOrNull() ?: 0

    Text("Horas de riesgo", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    Row(Modifier.fillMaxWidth()) {
        conteos.forEachIndexed { i, cantidad ->
            val intensidad = if (maximo == 0) 0f else 0.15f + 0.85f * cantidad / maximo
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .padding(horizontal = 2.dp)
                        .background(color.copy(alpha = intensidad)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$cantidad", style = MaterialTheme.typography.bodySmall)
                }
                Text("${i * HORAS_POR_BLOQUE} h", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
    if (maximo > 0) {
        val inicio = conteos.indexOf(maximo) * HORAS_POR_BLOQUE
        Spacer(Modifier.height(4.dp))
        Text(
            "Más alertas entre las $inicio:00 y las ${inicio + HORAS_POR_BLOQUE}:00. Procura descansar antes de manejar a esa hora.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun TarjetaEvento(evento: EventoEntity, onVerEnMapa: (EventoEntity) -> Unit) {
    val hora = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(evento.fechaHora))
    val reaccion = evento.tiempoReaccionMs?.let { "%.1f s".format(it / 1000.0) } ?: "sin respuesta"

    Card(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Alerta nivel ${evento.nivelAlerta}", fontWeight = FontWeight.Bold)
                Text("$hora  ·  pulso ${evento.pulsoAlerta}/${evento.pulsoBase} lpm")
                Text("Reacción: $reaccion")
            }
            if (evento.latitud != null && evento.longitud != null) {
                TextButton(onClick = { onVerEnMapa(evento) }) { Text("Mapa") }
            }
        }
    }
}
