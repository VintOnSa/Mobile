package com.example.desarrollomovil.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desarrollomovil.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.String

class JuegoViewModel(private val repository: JuegoRepository) : ViewModel() {

    val todosLosJuegos = repository.obtenerTodosLosJuegos()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun agregarJuego(titulo: String,publicador: String,precio: Double, stock: Int,descripcion: String,plataforma: String,genero: String, imagenUri: String = "") {
        if (titulo.isBlank() || publicador.isBlank()) {
            _mensaje.value = "Título y Publicador son obligatorios"
            return
        }

        viewModelScope.launch {
            try {
                val nuevoJuego = Juego(
                    titulo = titulo,
                    publicador = publicador,
                    precio = precio,
                    stock = stock,
                    descripcion = descripcion,
                    plataforma = plataforma,
                    genero = genero,
                    imagenUri = imagenUri
                )
                repository.insertarJuego(nuevoJuego)
                _mensaje.value = "Juego agregado exitosamente"
                println("Mensaje ${mensaje}")
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            }
        }
    }

    suspend fun obtenerJuegoPorId(id: Long): Juego? {
        return try {
            repository.obtenerJuegoPorId(id)
        } catch (e: Exception) {
            _mensaje.value = "Error al obtener juego: ${e.message}"
            null
        }
    }

    fun actualizarJuego(
        id: Long,
        titulo: String,
        publicador: String,
        precio: Double,
        stock: Int,
        descripcion: String,
        plataforma: String,
        genero: String,
        imagenUri: String = ""
    ) {
        if (titulo.isBlank() || publicador.isBlank()) {
            _mensaje.value = "Titulo y Publicador son Obligatorios"
            return
        }

        viewModelScope.launch {
            try {
                val juegoActualizado = Juego(
                    id = id,
                    titulo = titulo,
                    publicador = publicador,
                    precio = precio,
                    stock = stock,
                    descripcion = descripcion,
                    plataforma = plataforma,
                    genero = genero,
                    imagenUri = imagenUri
                )
                repository.actualizarJuego(juegoActualizado)
                _mensaje.value = "Juego actualizado exitosamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar: ${e.message}"
            }
        }
    }

    fun eliminarJuego(id: Long) {
        viewModelScope.launch {
            try {
                repository.eliminarJuego(id)
                _mensaje.value = "Juego eliminado exitosamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar juego: ${e.message}"
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}