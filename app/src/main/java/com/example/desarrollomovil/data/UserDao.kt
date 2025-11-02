package com.example.desarrollomovil.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun registro(usuario: User): Long

    @Query("SELECT * FROM usuarios WHERE correo= :correo AND password = :password ")
    suspend fun login(correo: String, password: String): User?

    @Query("SELECT * FROM usuarios WHERE correo = :correo")
    suspend fun getUserByEmail(correo: String): User?

    @Query("SELECT COUNT(*) FROM usuarios WHERE tipo = 'admin'")
    suspend fun adminExists(): Int



}
