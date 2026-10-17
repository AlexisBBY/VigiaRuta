package com.alexis.vigiaruta.domain

sealed interface ResultadoDeteccion {
    data class Calibrando(val segundosRestantes: Int) : ResultadoDeteccion
    data class Normal(val pulso: Int, val pulsoBase: Int) : ResultadoDeteccion
    data class Fatiga(val pulso: Int, val pulsoBase: Int) : ResultadoDeteccion
}
