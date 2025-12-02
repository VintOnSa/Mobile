package com.example.desarrollomovil.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desarrollomovil.data.*
import com.example.desarrollomovil.repository.JuegoService
import com.example.desarrollomovil.vistas.vibrar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.String

class JuegoViewModel : ViewModel() {

    private val JuegosService = JuegoService.instance


    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()


    var state by mutableStateOf(JuegoState())


    init {
        viewModelScope.launch {
            ObtenerJuegos()
        }
    }


    fun cID(nID:Int){
        state = state.copy(id = nID)
    }
    fun cTitulo(nTitulo:String){
        state = state.copy(titulo = nTitulo)
    }
    fun cPublicador(nPublicador:String){
        state = state.copy(publicador = nPublicador)
    }
    fun cPrecio(nPrecio:String){
        state = state.copy(precio = nPrecio)
    }
    fun cStock(nStock:String){
        state = state.copy(stock = nStock)
    }
    fun cDesc(nDesc:String){
        state = state.copy(descripcion = nDesc)
    }
    fun cPlataforma(nPlataforma:String){
        state = state.copy(plataforma= nPlataforma)
    }
    fun cGenero(nGenero:String){
        state = state.copy(genero = nGenero)
    }
    fun cImagen(nImagen:String){
        state = state.copy(imagenurl = nImagen)
    }

    suspend fun ObtenerJuegos(){
        try{
            val JuegosEncontrados = JuegosService.obtenerJuegos()
            state = state.copy(juego = JuegosEncontrados)
        }catch (e: Exception){
            _mensaje.value = "No se encontraron Juegos Error: ${e.message}"
        }
    }
    fun agregarJuego(imagenFile: java.io.File? = null) {
        viewModelScope.launch {
            try {

                var urlImagen = state.imagenurl ?: ""

                imagenFile?.let { file ->
                    val imagenPart = MultipartBody.Part.createFormData(
                        "imagen",
                        file.name,
                        file.asRequestBody("image/*".toMediaType())
                    )
                    val respuesta = JuegosService.subirImagen(imagenPart)
                    urlImagen = respuesta.imageUrl
                }

                val nuevoJuego = JuegoAgregar(
                    titulo = state.titulo,
                    publicador = state.publicador,
                    precio = state.precio.toIntOrNull() ?: 0,
                    stock = state.stock.toIntOrNull() ?: 0,
                    descripcion = state.descripcion,
                    plataforma = state.plataforma,
                    genero = state.genero,
                    imagenurl = urlImagen
                )
                JuegosService.agregarJuego(nuevoJuego)
                _mensaje.value = "Juego agregado exitosamente"
                println("Mensaje ${mensaje}")

                state = state.copy(
                    titulo = "",
                    publicador = "",
                    precio = "",
                    stock = "",
                    descripcion = "",
                    plataforma = "",
                    genero = "",
                    imagenurl = ""
                )

            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            }
        }
    }

    suspend fun obtenerJuegoPorId(id: Int): Juego {
        val juegoEncontrado = JuegosService.buscarJuego(id)
        try {
            cTitulo(juegoEncontrado.titulo)
            cPublicador(juegoEncontrado.publicador)
            cPrecio(juegoEncontrado.precio.toString())
            cStock(juegoEncontrado.stock.toString())
            cDesc(juegoEncontrado.descripcion)
            cPlataforma(juegoEncontrado.plataforma)
            cGenero(juegoEncontrado.genero)
            cImagen(juegoEncontrado.imagenurl)

        } catch (e: Exception) {
            _mensaje.value = "Error en Busqueda - Error: ${e.message}"
        }
        return juegoEncontrado
    }

    fun actualizarJuego(juego: Juego, imagenFile: java.io.File? = null) {
        viewModelScope.launch {
            try {
                var urlImagen = juego.imagenurl ?: ""

                imagenFile?.let { file ->
                    val imagenPart = MultipartBody.Part.createFormData(
                        "imagen",
                        file.name,
                        file.asRequestBody("image/*".toMediaType())
                    )
                    val respuesta = JuegosService.subirImagen(imagenPart)
                    urlImagen = respuesta.imageUrl
                }
                var juegoEditado = juego.copy(imagenurl = urlImagen)

                state = state.copy(
                    titulo = juegoEditado.titulo,
                    publicador = juegoEditado.publicador,
                    precio = juegoEditado.precio.toString(),
                    stock = juegoEditado.stock.toString(),
                    descripcion = juegoEditado.descripcion,
                    plataforma = juegoEditado.plataforma,
                    genero = juegoEditado.genero,
                    imagenurl = urlImagen
                )
                JuegosService.actualizarJuego(juegoEditado)
                _mensaje.value = "Juego actualizado exitosamente"

                state = state.copy(
                    titulo = "",
                    publicador = "",
                    precio = "",
                    stock = "",
                    descripcion = "",
                    plataforma = "",
                    genero = "",
                    imagenurl = ""
                )

            }catch (e: Exception){
                _mensaje.value = "Error al actualizar: ${e.message}"
            }
        }
    }

    fun eliminarJuego(id: Int) {
        viewModelScope.launch {
            try {
                JuegosService.eliminarJuego(id)
                _mensaje.value = "Juego eliminado exitosamente"
                ObtenerJuegos()
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar juego: ${e.message}"
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

}