package com.alexis.vigiaruta.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.alexis.vigiaruta.shared.EventoFatiga
import com.alexis.vigiaruta.ui.MainActivity

object Notificador {

    private const val CANAL = "alertas_reloj"

    fun crearCanal(context: Context) {
        val canal = NotificationChannel(CANAL, "Alertas del reloj", NotificationManager.IMPORTANCE_HIGH)
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, evento: EventoFatiga) {
        val permitido = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!permitido) return

        val abrir = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val reaccion = evento.tiempoReaccionMs?.let { "reaccion de ${it / 1000.0} s" } ?: "sin respuesta"

        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Alerta de fatiga nivel ${evento.nivelAlerta}")
            .setContentText("Pulso ${evento.pulsoAlerta} lpm, $reaccion")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(abrir)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(evento.fechaHora.toInt(), notificacion)
    }
}
