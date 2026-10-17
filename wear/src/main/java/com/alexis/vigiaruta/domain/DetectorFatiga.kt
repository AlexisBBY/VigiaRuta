package com.alexis.vigiaruta.domain

import kotlin.math.roundToInt

class DetectorFatiga(
    private val duracionCalibracionMs: Long = CALIBRACION_REAL_MS,
    private val caidaPulso: Double = 0.10,
    private val umbralMovimiento: Float = 0.6f,
    private val ventanaFatigaMs: Long = 10_000L
) {
    private var inicioMs: Long? = null
    private val pulsosCalibracion = mutableListOf<Int>()
    private var inicioSospechaMs: Long? = null

    var pulsoBase: Int? = null
        private set

    fun procesar(lectura: LecturaSensor): ResultadoDeteccion {
        val inicio = inicioMs ?: lectura.tiempoMs.also { inicioMs = it }
        val base = pulsoBase ?: return calibrar(lectura, inicio)

        val limitePulso = (base * (1 - caidaPulso)).toInt()
        val pulsoBajo = lectura.pulso in 1..limitePulso
        val munecaQuieta = lectura.movimiento < umbralMovimiento

        if (pulsoBajo && munecaQuieta) {
            val desde = inicioSospechaMs ?: lectura.tiempoMs.also { inicioSospechaMs = it }
            if (lectura.tiempoMs - desde >= ventanaFatigaMs) {
                inicioSospechaMs = null
                return ResultadoDeteccion.Fatiga(lectura.pulso, base)
            }
        } else {
            inicioSospechaMs = null
        }
        return ResultadoDeteccion.Normal(lectura.pulso, base)
    }

    private fun calibrar(lectura: LecturaSensor, inicio: Long): ResultadoDeteccion {
        if (lectura.pulso > 0) pulsosCalibracion += lectura.pulso
        val transcurrido = lectura.tiempoMs - inicio

        if (transcurrido >= duracionCalibracionMs && pulsosCalibracion.isNotEmpty()) {
            val base = pulsosCalibracion.average().roundToInt()
            pulsoBase = base
            return ResultadoDeteccion.Normal(lectura.pulso, base)
        }
        val restante = (duracionCalibracionMs - transcurrido).coerceAtLeast(0) / 1000
        return ResultadoDeteccion.Calibrando(restante.toInt())
    }

    companion object {
        const val CALIBRACION_REAL_MS = 5 * 60_000L
        const val CALIBRACION_SIMULADA_MS = 20_000L
    }
}
