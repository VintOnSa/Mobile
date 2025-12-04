package com.example.desarrollomovil.vistas

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.desarrollomovil.data.User
import com.example.desarrollomovil.data.UserRepository
import com.example.desarrollomovil.viewmodels.UserViewModel
import com.example.desarrollomovil.viewmodels.UserViewModelFactory
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Mocks
    private val mockUserRepository = mockk<UserRepository>()
    private val mockViewModel = mockk<UserViewModel>(relaxed = true)
    private val userLoginSuccessSlot = slot<User>()

    // Simular el estado interno del ViewModel
    private val mensajeFlow = MutableStateFlow<String?>(null)

    @Before
    fun setup() {
        // Configurar el mock para el mensaje (usado por el Text de error)
        every { mockViewModel.mensaje } returns mensajeFlow

        // Configurar el slot para capturar el argumento de onLoginSuccess
        every { mockViewModel.limpiarMensaje() } just runs
    }

    @Test
    fun loginScreen_muestraElementosYBotones() {
        // 1. Arrange: Cargar la pantalla de Login
        composeTestRule.setContent {
            // Usamos un proveedor para inyectar nuestro mock (simplificado para la prueba)
            LoginScreen(
                onLoginSuccess = {},
                toRegistro = {},
                // Reemplazando el viewModel real con el mock
                userViewModel = mockViewModel
            )
        }

        // 2. Assert: Verificar que los elementos clave están presentes
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Usuario").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
        composeTestRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed().assertIsNotEnabled() // Inicialmente deshabilitado
        composeTestRule.onNodeWithText("Registro").assertIsDisplayed()
    }

    @Test
    fun loginScreen_camposVacios_botonIniciarSesionDeshabilitado() {
        // 1. Arrange
        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                toRegistro = {},
                userViewModel = mockViewModel
            )
        }

        // 2. Assert: El botón "Iniciar Sesión" debe estar deshabilitado
        composeTestRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed().assertIsNotEnabled()
    }

    @Test
    fun loginScreen_credencialesValidas_loginExitoso_llamaCallback() {
        // 1. Arrange
        val usuarioLogeado = User(nombre = "Test", correo = "a@a.com", password = "p", tipo = "usuario")
        var loginSuccessCalled = false

        coEvery { mockViewModel.login("test@mail.com", "pass123") } returns usuarioLogeado // Simular éxito

        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = { user ->
                    loginSuccessCalled = true
                    userLoginSuccessSlot.captured = user
                },
                toRegistro = {},
                userViewModel = mockViewModel
            )
        }

        // 2. Act: Ingresar datos
        composeTestRule.onNodeWithText("Usuario").performTextInput("test@mail.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("pass123")

        // 3. Act: Click en Iniciar Sesión
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()

        // 4. Assert: Verificar que el callback se llamó con el usuario correcto
        assert(loginSuccessCalled) { "onLoginSuccess no fue llamado" }
        assertEquals(usuarioLogeado, userLoginSuccessSlot.captured)
    }

    @Test
    fun loginScreen_loginFallido_muestraMensajeError() {
        // 1. Arrange
        coEvery { mockViewModel.login(any(), any()) } returns null
        coEvery { mockViewModel.verificarUsuarioExiste("user@test.com") } returns true // Usuario existe, contraseña incorrecta

        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                toRegistro = {},
                userViewModel = mockViewModel
            )
        }

        // 2. Act: Ingresar datos y click en Iniciar Sesión
        composeTestRule.onNodeWithText("Usuario").performTextInput("user@test.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("wrongpass")
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()

        // 3. Assert: Verificar que el mensaje de error de contraseña incorrecta se muestra
        composeTestRule.onNodeWithText("Contraseña incorrecta. Inténtalo de nuevo.").assertIsDisplayed()
    }

    @Test
    fun loginScreen_clickRegistro_llamaCallback() {
        // 1. Arrange
        var toRegistroCalled = false
        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                toRegistro = { toRegistroCalled = true },
                userViewModel = mockViewModel
            )
        }

        // 2. Act: Click en el botón de Registro
        composeTestRule.onNodeWithText("Registro").performClick()

        // 3. Assert: Verificar que el callback toRegistro fue llamado
        assert(toRegistroCalled) { "toRegistro no fue llamado" }
    }
}