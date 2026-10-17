package com.alexis.vigiaruta.data.comunicacion

import android.content.Context
import android.util.Log
import com.alexis.vigiaruta.shared.EventoFatiga
import com.alexis.vigiaruta.shared.EventoMapper
import com.alexis.vigiaruta.shared.RutasDataLayer
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

class EnviadorEventos(context: Context) {

    private val nodeClient = Wearable.getNodeClient(context)
    private val messageClient = Wearable.getMessageClient(context)

    suspend fun enviar(evento: EventoFatiga): Int {
        return try {
            val nodos = nodeClient.connectedNodes.await()
            val datos = EventoMapper.aBytes(evento)
            nodos.forEach { nodo ->
                messageClient.sendMessage(nodo.id, RutasDataLayer.EVENTO_FATIGA, datos).await()
            }
            nodos.size
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo enviar el evento", e)
            0
        }
    }

    private companion object {
        const val TAG = "EnviadorEventos"
    }
}
