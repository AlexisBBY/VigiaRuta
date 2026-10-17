package com.alexis.vigiaruta.data.sensor

import com.alexis.vigiaruta.domain.LecturaSensor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class SensoresSimulados : FuenteSensores {

    override fun lecturas(): Flow<LecturaSensor> = flow {
        var segundo = 0
        while (true) {
            val somnoliento = segundo >= 40 && (segundo - 40) % 60 < 30
            val pulso = if (somnoliento) 64 + Random.nextInt(-1, 2) else 78 + Random.nextInt(-2, 3)
            val movimiento = if (somnoliento) 0.1f else 1.0f + Random.nextFloat()
            emit(LecturaSensor(pulso, movimiento, System.currentTimeMillis()))
            delay(1_000L)
            segundo++
        }
    }
}
