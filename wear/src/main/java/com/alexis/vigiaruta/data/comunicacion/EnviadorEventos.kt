package com.alexis.vigiaruta.data.comunicacion

import android.content.Context
import android.util.Log
import com.alexis.vigiaruta.shared.EventoFatiga
import com.alexis.vigiaruta.shared.EventoMapper
import com.alexis.vigiaruta.shared.RutasDataLayer
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import org.json.JSONArray

class EnviadorEventos(context: Context) {

    private val nodeClient = Wearable.getNodeClient(context)
    private val messageClient = Wearable.getMessageClient(context)
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val candado = Mutex()

    suspend fun enviar(evento: EventoFatiga): Boolean {
        val json = String(EventoMapper.aBytes(evento), Charsets.UTF_8)
        val enviado = mandar(json)
        if (enviado) {
            reenviarPendientes()
        } else {
            guardarPendiente(json)
        }
        return enviado
    }

    suspend fun reenviarPendientes() = candado.withLock {
        val pendientes = leerPendientes()
        if (pendientes.isEmpty()) return@withLock
        val restantes = pendientes.filterNot { mandar(it) }
        escribirPendientes(restantes)
    }

    fun cantidadPendientes(): Int = leerPendientes().size

    private suspend fun mandar(json: String): Boolean {
        return try {
            val nodos = nodeClient.connectedNodes.await()
            if (nodos.isEmpty()) return false
            val datos = json.toByteArray(Charsets.UTF_8)
            nodos.forEach { nodo ->
                messageClient.sendMessage(nodo.id, RutasDataLayer.EVENTO_FATIGA, datos).await()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo enviar el evento", e)
            false
        }
    }

    private suspend fun guardarPendiente(json: String) = candado.withLock {
        escribirPendientes(leerPendientes() + json)
    }

    private fun leerPendientes(): List<String> {
        val texto = prefs.getString(LLAVE, null) ?: return emptyList()
        val arreglo = JSONArray(texto)
        return List(arreglo.length()) { arreglo.getString(it) }
    }

    private fun escribirPendientes(lista: List<String>) {
        prefs.edit().putString(LLAVE, JSONArray(lista).toString()).apply()
    }

    private companion object {
        const val TAG = "EnviadorEventos"
        const val PREFS = "alertas_pendientes"
        const val LLAVE = "lista"
    }
}
