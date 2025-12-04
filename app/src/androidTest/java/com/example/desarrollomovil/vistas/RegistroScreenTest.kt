package com.example.desarrollomovil.vistas

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.desarrollomovil.viewmodels.UserViewModel
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistroScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<UserViewModel>(relaxed = true)
    private val mensajeFlow = MutableStateFlow<String?>(null)

    @Before
    fun setup() {
        // Configurar el mock para el mensaje (usado por el Text de error)
        every { mockViewModel.mensaje } returns mensajeFlow
        every { mockViewModel.limpiarMensaje() } just runs
    }

    @Test
    fun registroScreen_muestraTodosLosCamposYBotones() {
        // 1. Arrange: Cargar la pantalla de Registro
        composeTestRule.setContent {
            RegistroScreen(
                onRegistroExitoso = {},
                toLogin = {},
                userViewModel = mockViewModel
            )
        }

        // 2. Assert: Verificar que los elementos clave están presentes
        composeTestRule.onNodeWithText("Registro").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nombre").assertIsDisplayed()
        composeTestRule.onNodeWithText("Correo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
        composeTestRule.onNodeWithText("Registrarse").assertIsDisplayed().assertIsNotEnabled() // Inicialmente deshabilitado
        composeTestRule.onNodeWithText("Volver a Login").assertIsDisplayed()
    }

    @Test
    fun registroScreen_camposIncompletos_botonRegistrarseDeshabilitado() {
        // 1. Arrange
        composeTestRule.setContent {
            RegistroScreen(
                onRegistroExitoso = {},
                toLogin = {},
                userViewModel = mockViewModel
            )
        }

        // 2. Act: Ingresar solo el nombre
        composeTestRule.onNodeWithText("Nombre").performTextInput("Usuario Test")

        // 3. Assert: El botón "Registrarse" debe seguir deshabilitado
        composeTestRule.onNodeWithText("Registrarse").assertIsNotEnabled()
    }

    @Test
    fun registroScreen_camposValidos_registroExitoso_llamaCallback() {
        // 1. Arrange
        var registroSuccessCalled = false

        // Simular que el registro en el ViewModel será exitoso
        coEvery { mockViewModel.registrarUsuario(any(), any(), any()) } returns true

        composeTestRule.setContent {
            RegistroScreen(
                onRegistroExitoso = { registroSuccessCalled = true },
                toLogin = {},
                userViewModel = mockViewModel
            )
        }

        // 2. Act: Ingresar datos válidos
        composeTestRule.onNodeWithText("Nombre").performTextInput("Usuario Test")
        composeTestRule.onNodeWithText("Correo").performTextInput("test@mail.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")

        // 3. Act: Click en Registrarse
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // 4. Assert: Verificar que el callback se llamó
        assert(registroSuccessCalled) { "onRegistroExitoso no fue llamado" }
    }

    @Test
    fun registroScreen_registroFallido_muestraMensajeErrorDelViewModel() {
        // 1. Arrange
        val errorMessage = "La contraseña es muy débil"
        // Simular que el registro falló y el ViewModel emite un mensaje
        coEvery { mockViewModel.registrarUsuario(any(), any(), any()) } returns false
        mensajeFlow.value = errorMessage // Emitir el error

        composeTestRule.setContent {
            RegistroScreen(
                onRegistroExitoso = {},
                toLogin = {},
                userViewModel = mockViewModel
            )
        }

        // 2. Act: Ingresar datos y click
        composeTestRule.onNodeWithText("Nombre").performTextInput("Usuario Test")
        composeTestRule.onNodeWithText("Correo").performTextInput("fail@mail.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("short") // Contraseña inválida
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // 3. Assert: Verificar que el mensaje de error se muestra
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun registroScreen_clickVolverLogin_llamaCallback() {
        // 1. Arrange
        var toLoginCalled = false
        composeTestRule.setContent {
            RegistroScreen(
                onRegistroExitoso = {},
                toLogin = { toLoginCalled = true },
                userViewModel = mockViewModel
            )
        }

        // 2. Act: Click en el botón de Volver a Login
        composeTestRule.onNodeWithText("Volver a Login").performClick()

        // 3. Assert: Verificar que el callback toLogin fue llamado
        assert(toLoginCalled) { "toLogin no fue llamado" }
    }
}