package com.example.desarrollomovil.data

import kotlinx.coroutines.flow.Flow

class JuegoRepository(private val juegoDao: JuegoDao) {
    suspend fun insertarJuego(juego: Juego): Long {
        return juegoDao.insertarJuego(juego)
    }

    fun obtenerTodosLosJuegos(): Flow<List<Juego>> {
        return juegoDao.obtenerTodosLosJuegos()
    }

    suspend fun obtenerJuegoPorId(id: Long): Juego? {
        return juegoDao.obtenerJuegoPorId(id)
    }

    suspend fun actualizarJuego(juego: Juego) {
        juegoDao.actualizarJuego(juego)
    }

    suspend fun eliminarJuego(id: Long) {
        juegoDao.eliminarJuego(id)
    }
}