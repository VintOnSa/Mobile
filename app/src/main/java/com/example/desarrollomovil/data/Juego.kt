package com.example.desarrollomovil.data

import com.squareup.moshi.Json

data class Juego(
    @field:Json("id")
    val id: Int,

    @field:Json("titulo")
    val titulo: String,

    @field:Json("publicador")
    val publicador: String,

    @field:Json("precio")
    val precio: Int,

    @field:Json("stock")
    val stock: Int,

    @field:Json("descripcion")
    val descripcion: String,

    @field:Json("plataforma")
    val plataforma: String,

    @field:Json("genero")
    val genero: String,

    @field:Json("imagenurl")
    val imagenurl: String
)

data class JuegoState(
    val juego: List<Juego> = emptyList(),
    val id: Int? = null,
    val titulo: String = "",
    val publicador: String = "",
    val precio: String = "",
    val stock: String = "",
    val descripcion: String = "",
    val plataforma: String = "",
    val genero: String = "",
    val imagenurl: String = ""
)


data class JuegoAgregar(
    @field:Json("titulo")
    val titulo: String,

    @field:Json("publicador")
    val publicador: String,

    @field:Json("precio")
    val precio: Int,

    @field:Json("stock")
    val stock: Int,

    @field:Json("descripcion")
    val descripcion: String,

    @field:Json("plataforma")
    val plataforma: String,

    @field:Json("genero")
    val genero: String,

    @field:Json("imagenurl")
    val imagenurl: String
)