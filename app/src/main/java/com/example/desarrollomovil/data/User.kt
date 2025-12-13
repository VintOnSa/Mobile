package com.example.desarrollomovil.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var correo: String,
    var password: String,
    var nombre: String,
    var tipo: String = "usuario"
)