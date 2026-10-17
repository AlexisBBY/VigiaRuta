package com.alexis.vigiaruta.shared

data class EventoFatiga(
    val fechaHora: Long,
    val pulsoBase: Int,
    val pulsoAlerta: Int,
    val nivelAlerta: Int,
    val tiempoReaccionMs: Long?
)
