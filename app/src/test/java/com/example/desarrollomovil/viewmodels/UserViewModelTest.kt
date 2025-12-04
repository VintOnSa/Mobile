package com.example.desarrollomovil.viewmodels

import com.example.desarrollomovil.data.User
import com.example.desarrollomovil.data.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UserViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val userRepository: UserRepository = mockk()
    private lateinit var viewModel: UserViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = UserViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun registrarUsuario_camposVacios_estableceMensajeError() = runTest {
        // Ejecutar con campos vacíos
        viewModel.registrarUsuario("", "correo@mail.com", "password")

        // Verificar el mensaje de error
        val mensaje = viewModel.mensaje.first()
        assertEquals("Todos los campos son obligatorios", mensaje)
    }

    @Test
    fun registrarUsuario_contrasenaCorta_estableceMensajeError() = runTest {
        // Ejecutar con contraseña corta (menos de 6 caracteres)
        viewModel.registrarUsuario("TestUser", "correo@mail.com", "pass")

        // Verificar el mensaje de error
        val mensaje = viewModel.mensaje.first()
        assertEquals("La contraseña debe tener al menos 6 caracteres", mensaje)
    }

    @Test
    fun registrarUsuario_correoExistente_estableceMensajeError() = runTest {
        // Configurar el mock para simular que el usuario ya existe
        coEvery { userRepository.verificarUsuarioExiste("existente@mail.com") } returns true

        // Ejecutar el registro
        viewModel.registrarUsuario("TestUser", "existente@mail.com", "password123")

        // Verificar el mensaje de error
        val mensaje = viewModel.mensaje.first()
        assertEquals("Este correo ya esta registrado", mensaje)
    }

    @Test
    fun login_credencialesCorrectas_retornaUsuario() = runTest {
        val usuarioEsperado = User(nombre = "Admin", correo = "admin@mail.com", password = "admin123", tipo = "admin")
        // Configurar el mock para simular un login exitoso
        coEvery { userRepository.login("admin@mail.com", "admin123") } returns usuarioEsperado

        // Ejecutar el login
        val usuarioActual = viewModel.login("admin@mail.com", "admin123")

        // Verificar el resultado
        assertEquals(usuarioEsperado, usuarioActual)
    }

    @Test
    fun login_credencialesIncorrectas_retornaNull() = runTest {
        // Configurar el mock para simular un login fallido
        coEvery { userRepository.login("admin@mail.com", "wrongpass") } returns null

        // Ejecutar el login
        val usuarioActual = viewModel.login("admin@mail.com", "wrongpass")

        // Verificar el resultado
        assertEquals(null, usuarioActual)
    }
}