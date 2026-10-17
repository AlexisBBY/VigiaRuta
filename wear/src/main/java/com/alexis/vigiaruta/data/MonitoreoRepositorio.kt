package com.alexis.vigiaruta.data

import com.alexis.vigiaruta.data.comunicacion.EnviadorEventos
import com.alexis.vigiaruta.domain.DetectorFatiga
import com.alexis.vigiaruta.domain.LecturaSensor
import com.alexis.vigiaruta.domain.ResultadoDeteccion
import com.alexis.vigiaruta.shared.EventoFatiga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MonitoreoRepositorio(
    private val alertador: Alertador,
    private val enviador: EnviadorEventos,
    private val scope: CoroutineScope
) {
    private val _estado = MutableStateFlow<EstadoMonitoreo>(EstadoMonitoreo.Inactivo)
    val estado: StateFlow<EstadoMonitoreo> = _estado.asStateFlow()

    private var detector: DetectorFatiga? = null
    private var escalamiento: Job? = null

    fun iniciar(nuevoDetector: DetectorFatiga) {
        detector = nuevoDetector
        _estado.value = EstadoMonitoreo.Calibrando(segundosRestantes = 0)
    }

    fun procesar(lectura: LecturaSensor) {
        if (_estado.value is EstadoMonitoreo.Alerta) return
        val resultado = detector?.procesar(lectura) ?: return

        _estado.value = when (resultado) {
            is ResultadoDeteccion.Calibrando -> EstadoMonitoreo.Calibrando(resultado.segundosRestantes)
            is ResultadoDeteccion.Normal -> EstadoMonitoreo.Monitoreando(resultado.pulso, resultado.pulsoBase)
            is ResultadoDeteccion.Fatiga -> {
                lanzarAlerta(resultado)
                return
            }
        }
    }

    private fun lanzarAlerta(fatiga: ResultadoDeteccion.Fatiga) {
        _estado.value = EstadoMonitoreo.Alerta(fatiga.pulsoBase, fatiga.pulso, nivel = 1, inicioMs = System.currentTimeMillis())
        alertador.alertar(1)

        escalamiento = scope.launch {
            delay(TIEMPO_MAXIMO_REACCION_MS)
            val alerta = _estado.value as? EstadoMonitoreo.Alerta ?: return@launch
            _estado.value = alerta.copy(nivel = 2)
            alertador.alertar(2)
        }
    }

    fun registrarReaccion() {
        val alerta = _estado.value as? EstadoMonitoreo.Alerta ?: return
        val tiempoReaccion = System.currentTimeMillis() - alerta.inicioMs
        cerrarAlerta(alerta, tiempoReaccion)
        _estado.value = EstadoMonitoreo.Monitoreando(alerta.pulsoAlerta, alerta.pulsoBase)
    }

    fun detener() {
        (_estado.value as? EstadoMonitoreo.Alerta)?.let { cerrarAlerta(it, tiempoReaccionMs = null) }
        detector = null
        _estado.value = EstadoMonitoreo.Inactivo
    }

    private fun cerrarAlerta(alerta: EstadoMonitoreo.Alerta, tiempoReaccionMs: Long?) {
        escalamiento?.cancel()
        alertador.detener()
        val evento = EventoFatiga(
            fechaHora = alerta.inicioMs,
            pulsoBase = alerta.pulsoBase,
            pulsoAlerta = alerta.pulsoAlerta,
            nivelAlerta = alerta.nivel,
            tiempoReaccionMs = tiempoReaccionMs
        )
        scope.launch { enviador.enviar(evento) }
    }

    companion object {
        const val TIEMPO_MAXIMO_REACCION_MS = 3_000L
    }
}
