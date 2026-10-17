package com.alexis.vigiaruta.data

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

interface Alertador {
    fun alertar(nivel: Int)
    fun detener()
}

class AlertadorAndroid(context: Context) : Alertador {

    private val vibrator: Vibrator =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Vibrator::class.java)
        }
    private var tono: ToneGenerator? = null

    override fun alertar(nivel: Int) {
        val patron = longArrayOf(0, 600, 200, 600, 200, 600)
        val repetir = if (nivel >= 2) 0 else -1
        vibrator.vibrate(VibrationEffect.createWaveform(patron, repetir))

        if (nivel >= 2) {
            tono = (tono ?: ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)).also {
                it.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK)
            }
        }
    }

    override fun detener() {
        vibrator.cancel()
        tono?.stopTone()
        tono?.release()
        tono = null
    }
}
