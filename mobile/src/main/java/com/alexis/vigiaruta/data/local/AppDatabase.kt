package com.alexis.vigiaruta.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EventoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventoDao(): EventoDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vigiaruta.db"
                ).build().also { instancia = it }
            }
    }
}
