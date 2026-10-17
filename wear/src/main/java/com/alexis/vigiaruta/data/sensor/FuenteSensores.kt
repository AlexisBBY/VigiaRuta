package com.alexis.vigiaruta.data.sensor

import com.alexis.vigiaruta.domain.LecturaSensor
import kotlinx.coroutines.flow.Flow

interface FuenteSensores {
    fun lecturas(): Flow<LecturaSensor>
}
