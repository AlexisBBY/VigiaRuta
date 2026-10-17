package com.alexis.vigiaruta.data.repository

import com.alexis.vigiaruta.data.local.EventoDao
import com.alexis.vigiaruta.data.local.EventoEntity
import com.alexis.vigiaruta.data.ubicacion.ProveedorUbicacion
import com.alexis.vigiaruta.shared.EventoFatiga
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class EventoRepository(
    private val dao: EventoDao,
    private val ubicacion: ProveedorUbicacion
) {

    suspend fun guardar(evento: EventoFatiga): Long {
        val lugar = ubicacion.ultimaUbicacion()
        return dao.insertar(
            EventoEntity(
                fechaHora = evento.fechaHora,
                pulsoBase = evento.pulsoBase,
                pulsoAlerta = evento.pulsoAlerta,
                nivelAlerta = evento.nivelAlerta,
                tiempoReaccionMs = evento.tiempoReaccionMs,
                latitud = lugar?.latitude,
                longitud = lugar?.longitude
            )
        )
    }

    fun observarUltimosDias(dias: Int = 5): Flow<List<EventoEntity>> {
        val desde = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(dias.toLong())
        return dao.observarDesde(desde)
    }
}
