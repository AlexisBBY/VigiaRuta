package com.alexis.vigiaruta.shared

import org.json.JSONObject

object EventoMapper {

    private const val FECHA = "fechaHora"
    private const val PULSO_BASE = "pulsoBase"
    private const val PULSO_ALERTA = "pulsoAlerta"
    private const val NIVEL = "nivelAlerta"
    private const val REACCION = "tiempoReaccionMs"

    fun aBytes(evento: EventoFatiga): ByteArray {
        val json = JSONObject()
            .put(FECHA, evento.fechaHora)
            .put(PULSO_BASE, evento.pulsoBase)
            .put(PULSO_ALERTA, evento.pulsoAlerta)
            .put(NIVEL, evento.nivelAlerta)
            .put(REACCION, evento.tiempoReaccionMs ?: JSONObject.NULL)
        return json.toString().toByteArray(Charsets.UTF_8)
    }

    fun desdeBytes(bytes: ByteArray): EventoFatiga {
        val json = JSONObject(String(bytes, Charsets.UTF_8))
        return EventoFatiga(
            fechaHora = json.getLong(FECHA),
            pulsoBase = json.getInt(PULSO_BASE),
            pulsoAlerta = json.getInt(PULSO_ALERTA),
            nivelAlerta = json.getInt(NIVEL),
            tiempoReaccionMs = if (json.isNull(REACCION)) null else json.getLong(REACCION)
        )
    }
}
