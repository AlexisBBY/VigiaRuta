package com.alexis.vigiaruta.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {

    @Insert
    suspend fun insertar(evento: EventoEntity): Long

    @Query("SELECT * FROM evento_fatiga WHERE fecha_hora >= :desde ORDER BY fecha_hora DESC")
    fun observarDesde(desde: Long): Flow<List<EventoEntity>>
}
