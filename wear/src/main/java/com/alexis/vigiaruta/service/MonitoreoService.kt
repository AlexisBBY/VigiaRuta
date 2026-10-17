package com.alexis.vigiaruta.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.alexis.vigiaruta.data.EstadoMonitoreo
import com.alexis.vigiaruta.di.ServiceLocator
import com.alexis.vigiaruta.ui.MainActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MonitoreoService : LifecycleService() {

    private val repositorio by lazy { ServiceLocator.monitoreoRepositorio }
    private var lectura: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (intent?.action == ACCION_DETENER) {
            repositorio.detener()
            stopSelf()
            return START_NOT_STICKY
        }

        crearCanales()
        ServiceCompat.startForeground(
            this,
            ID_NOTIF_SERVICIO,
            notificacionServicio(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
        )

        if (lectura == null) {
            repositorio.iniciar(ServiceLocator.crearDetector())
            lectura = lifecycleScope.launch {
                ServiceLocator.fuenteSensores().lecturas().collect { repositorio.procesar(it) }
            }
            lifecycleScope.launch {
                repositorio.estado.collect { estado ->
                    if (estado is EstadoMonitoreo.Alerta) mostrarAlerta(estado.nivel) else cancelarAlerta()
                }
            }
        }
        return START_STICKY
    }

    private fun abrirApp(): PendingIntent = PendingIntent.getActivity(
        this, 0,
        Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun notificacionServicio(): Notification =
        NotificationCompat.Builder(this, CANAL_SERVICIO)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("VigiaRuta")
            .setContentText("Monitoreando al conductor")
            .setOngoing(true)
            .setContentIntent(abrirApp())
            .build()

    private fun mostrarAlerta(nivel: Int) {
        val notificacion = NotificationCompat.Builder(this, CANAL_ALERTA)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Despierta")
            .setContentText("Alerta de fatiga nivel $nivel")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(abrirApp(), true)
            .setAutoCancel(true)
            .build()
        getSystemService(NotificationManager::class.java).notify(ID_NOTIF_ALERTA, notificacion)
    }

    private fun cancelarAlerta() {
        getSystemService(NotificationManager::class.java).cancel(ID_NOTIF_ALERTA)
    }

    private fun crearCanales() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CANAL_SERVICIO, "Monitoreo", NotificationManager.IMPORTANCE_LOW)
        )
        manager.createNotificationChannel(
            NotificationChannel(CANAL_ALERTA, "Alertas de fatiga", NotificationManager.IMPORTANCE_HIGH)
        )
    }

    companion object {
        private const val CANAL_SERVICIO = "monitoreo"
        private const val CANAL_ALERTA = "alertas"
        private const val ID_NOTIF_SERVICIO = 1
        private const val ID_NOTIF_ALERTA = 2
        private const val ACCION_DETENER = "com.alexis.vigiaruta.DETENER"

        fun iniciar(context: Context) {
            context.startForegroundService(Intent(context, MonitoreoService::class.java))
        }

        fun detener(context: Context) {
            context.startService(
                Intent(context, MonitoreoService::class.java).setAction(ACCION_DETENER)
            )
        }
    }
}
