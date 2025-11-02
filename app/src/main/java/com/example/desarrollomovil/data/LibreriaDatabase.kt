package com.example.desarrollomovil.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [Juego::class, User::class],
    version = 2,
    exportSchema = false
)
abstract class LibreriaDatabase : RoomDatabase() {
    abstract fun juegoDao(): JuegoDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: LibreriaDatabase? = null

        fun getDatabase(context: Context): LibreriaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LibreriaDatabase::class.java,
                    "libreria_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}