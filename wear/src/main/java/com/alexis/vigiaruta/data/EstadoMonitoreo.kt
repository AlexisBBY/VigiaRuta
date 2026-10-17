package com.alexis.vigiaruta.data

sealed interface EstadoMonitoreo {
    data object Inactivo : EstadoMonitoreo
    data class Calibrando(val segundosRestantes: Int) : EstadoMonitoreo
    data class Monitoreando(val pulso: Int, val pulsoBase: Int) : EstadoMonitoreo
    data class Alerta(
        val pulsoBase: Int,
        val pulsoAlerta: Int,
        val nivel: Int,
        val inicioMs: Long
    ) : EstadoMonitoreo
}
