package com.alexis.vigiaruta.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evento_fatiga")
data class EventoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "fecha_hora") val fechaHora: Long,
    @ColumnInfo(name = "pulso_base") val pulsoBase: Int,
    @ColumnInfo(name = "pulso_alerta") val pulsoAlerta: Int,
    @ColumnInfo(name = "nivel_alerta") val nivelAlerta: Int,
    @ColumnInfo(name = "tiempo_reaccion_ms") val tiempoReaccionMs: Long?,
    val latitud: Double?,
    val longitud: Double?
)
