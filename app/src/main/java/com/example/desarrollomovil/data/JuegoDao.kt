package com.example.desarrollomovil.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JuegoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarJuego(juego: Juego): Long

    @Query("SELECT * FROM juegos ORDER BY id DESC")
    fun obtenerTodosLosJuegos(): Flow<List<Juego>>

    @Query("SELECT * FROM juegos WHERE id = :id")
    suspend fun obtenerJuegoPorId(id: Long): Juego?

    @Update
    suspend fun actualizarJuego(juego: Juego)

    @Query("DELETE FROM juegos WHERE id = :id")
    suspend fun eliminarJuego(id: Long)

    @Query("SELECT COUNT(*) FROM juegos")
    suspend fun contarJuegos(): Int
}
