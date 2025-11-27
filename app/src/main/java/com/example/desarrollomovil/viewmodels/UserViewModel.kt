package com.example.desarrollomovil.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desarrollomovil.data.User
import com.example.desarrollomovil.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun registrarUsuario(nombre: String, correo: String, password: String) {
        if (nombre.isBlank() || correo.isBlank() || password.isBlank()) {
            _mensaje.value = "Todos los campos son obligatorios"
            return
        }

        if (password.length < 6) {
            _mensaje.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        viewModelScope.launch {
            try {
                val usuarioExistente = repository.verificarUsuarioExiste(correo)
                if (usuarioExistente) {
                    _mensaje.value = "Este correo ya esta registrado"
                    return@launch
                }

                val nuevoUsuario = User(
                    nombre = nombre,
                    correo = correo,
                    password = password,
                    tipo = "usuario"
                )

                repository.registro(nuevoUsuario)
                _mensaje.value = "Usuario registrado exitosamente"

            } catch (e: Exception) {
                _mensaje.value = "Error al registrar: ${e.message}"
            }
        }
    }

    suspend fun login(correo: String, password: String): User? {
        return try {
            repository.login(correo, password)
        } catch (e: Exception) {
            _mensaje.value = "Error en login: ${e.message}"
            null
        }
    }

    suspend fun verificarUsuarioExiste(correo: String): Boolean {
        return repository.verificarUsuarioExiste(correo)
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}