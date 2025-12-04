package com.example.desarrollomovil.vistas // <-- ¡Debe coincidir EXACTAMENTE con el archivo Usuario.kt!

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.desarrollomovil.data.User // Asegúrate de que esta importación de 'User' sea correcta
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsuarioScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val loggedInUser = User(
        nombre = "Juan Pérez",
        correo = "juan.perez@test.com",
        password = "encrypted",
        tipo = "admin" // O 'usuario'
    )

    @Test
    fun usuarioScreen_muestraInformacionUsuarioYBotonLogout() {
        // 1. Arrange
        composeTestRule.setContent {
            // UsuarioScreen está disponible sin importación si el paquete coincide
            UsuarioScreen(
                user = loggedInUser,
                onLogout = {}
            )
        }

        // 2. Assert: Verificar que la información del usuario se muestra
        composeTestRule.onNodeWithText("Perfil de Usuario").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nombre: Juan Pérez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Correo: juan.perez@test.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tipo de Usuario: admin").assertIsDisplayed()

        // 3. Assert: Verificar la presencia del botón de Logout
        composeTestRule.onNodeWithText("Cerrar Sesión").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun usuarioScreen_clickLogout_llamaCallback() {
        // 1. Arrange
        var logoutCalled = false
        composeTestRule.setContent {
            UsuarioScreen(
                user = loggedInUser,
                onLogout = { logoutCalled = true }
            )
        }

        // 2. Act: Click en Cerrar Sesión
        composeTestRule.onNodeWithText("Cerrar Sesión").performClick()

        // 3. Assert: Verificar que el callback onLogout fue llamado
        assert(logoutCalled) { "onLogout no fue llamado" }
    }
}