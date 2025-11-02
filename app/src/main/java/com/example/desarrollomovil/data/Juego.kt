package com.example.desarrollomovil.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "juegos")
data class Juego(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val publicador: String,
    val precio: Double,
    val stock: Int,
    val descripcion: String,
    val plataforma: String,
    val genero: String,
    val imagenUri: String = ""
)