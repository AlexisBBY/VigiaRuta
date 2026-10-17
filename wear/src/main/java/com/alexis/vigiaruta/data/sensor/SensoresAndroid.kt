package com.alexis.vigiaruta.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.alexis.vigiaruta.domain.LecturaSensor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

class SensoresAndroid(context: Context) : FuenteSensores {

    private val sensorManager = context.getSystemService(SensorManager::class.java)

    override fun lecturas(): Flow<LecturaSensor> = callbackFlow {
        var ultimoPulso = 0
        var movimientoMaximo = 0f

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_HEART_RATE -> ultimoPulso = event.values[0].toInt()
                    Sensor.TYPE_ACCELEROMETER -> {
                        val (x, y, z) = event.values
                        val magnitud = sqrt(x * x + y * y + z * z)
                        movimientoMaximo = max(movimientoMaximo, abs(magnitud - SensorManager.GRAVITY_EARTH))
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        launch {
            while (isActive) {
                delay(INTERVALO_MS)
                trySend(LecturaSensor(ultimoPulso, movimientoMaximo, System.currentTimeMillis()))
                movimientoMaximo = 0f
            }
        }

        awaitClose { sensorManager.unregisterListener(listener) }
    }

    private companion object {
        const val INTERVALO_MS = 1_000L
    }
}
