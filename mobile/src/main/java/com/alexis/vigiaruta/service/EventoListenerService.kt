package com.alexis.vigiaruta.service

import android.util.Log
import com.alexis.vigiaruta.di.ServiceLocator
import com.alexis.vigiaruta.shared.EventoMapper
import com.alexis.vigiaruta.shared.RutasDataLayer
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class EventoListenerService : WearableListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(mensaje: MessageEvent) {
        if (mensaje.path != RutasDataLayer.EVENTO_FATIGA) return

        val evento = try {
            EventoMapper.desdeBytes(mensaje.data)
        } catch (e: Exception) {
            Log.e(TAG, "Mensaje con formato invalido", e)
            return
        }

        scope.launch {
            ServiceLocator.eventoRepository.guardar(evento)
            Notificador.mostrar(applicationContext, evento)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private companion object {
        const val TAG = "EventoListener"
    }
}
