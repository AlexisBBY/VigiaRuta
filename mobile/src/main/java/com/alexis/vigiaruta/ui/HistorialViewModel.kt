package com.alexis.vigiaruta.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.alexis.vigiaruta.data.local.EventoEntity
import com.alexis.vigiaruta.data.repository.EventoRepository
import com.alexis.vigiaruta.di.ServiceLocator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ResumenDia(
    val etiqueta: String,
    val alertas: Int,
    val reaccionPromedioSeg: Double?
)

data class HistorialUiState(
    val resumen: List<ResumenDia> = emptyList(),
    val eventos: List<EventoEntity> = emptyList()
)

class HistorialViewModel(repositorio: EventoRepository) : ViewModel() {

    val estado: StateFlow<HistorialUiState> = repositorio.observarUltimosDias(DIAS)
        .map { eventos -> HistorialUiState(resumirPorDia(eventos), eventos) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistorialUiState())

    private fun resumirPorDia(eventos: List<EventoEntity>): List<ResumenDia> {
        val zona = ZoneId.systemDefault()
        val formato = DateTimeFormatter.ofPattern("EEE", Locale("es", "MX"))
        val hoy = LocalDate.now(zona)
        val porDia = eventos.groupBy { Instant.ofEpochMilli(it.fechaHora).atZone(zona).toLocalDate() }

        return (DIAS - 1 downTo 0).map { atras ->
            val dia = hoy.minusDays(atras.toLong())
            val delDia = porDia[dia].orEmpty()
            val reacciones = delDia.mapNotNull { it.tiempoReaccionMs }
            ResumenDia(
                etiqueta = dia.format(formato),
                alertas = delDia.size,
                reaccionPromedioSeg = if (reacciones.isEmpty()) null else reacciones.average() / 1000
            )
        }
    }

    companion object {
        private const val DIAS = 5

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { HistorialViewModel(ServiceLocator.eventoRepository) }
        }
    }
}
