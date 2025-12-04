package com.example.desarrollomovil.vistas

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun adminScreen_muestraTituloYOpcionesAdministrativas() {
        // 1. Arrange
        composeTestRule.setContent {
            // ✅ CORRECCIÓN: Usamos el nombre correcto de la función Composable: Admin
            Admin(
                toHome = {},
                toAgregarJuego = {},
                toListaJuegos = {},
                onLogout = {}
            )
        }

        // 2. Assert: Verificar la presencia de elementos clave de administración
        composeTestRule.onNodeWithText("Panel de Administracion").assertIsDisplayed()
        composeTestRule.onNodeWithText("Agregar Juego").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Lista de Juegos").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Cerrar Sesion").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun adminScreen_clickAgregarJuego_llamaCallback() {
        // 1. Arrange
        var agregarJuegoCalled = false
        composeTestRule.setContent {
            Admin(
                toHome = {},
                toAgregarJuego = { agregarJuegoCalled = true }, // Capturamos la llamada
                toListaJuegos = {},
                onLogout = {}
            )
        }

        // 2. Act: Click en el botón
        composeTestRule.onNodeWithText("Agregar Juego").performClick()

        // 3. Assert: Verificar que el callback fue llamado
        assertTrue("toAgregarJuego debe ser llamado", agregarJuegoCalled)
    }

    @Test
    fun adminScreen_clickListaJuegos_llamaCallback() {
        // 1. Arrange
        var listaJuegosCalled = false
        composeTestRule.setContent {
            Admin(
                toHome = {},
                toAgregarJuego = {},
                toListaJuegos = { listaJuegosCalled = true }, // Capturamos la llamada
                onLogout = {}
            )
        }

        // 2. Act: Click en el botón
        composeTestRule.onNodeWithText("Lista de Juegos").performClick()

        // 3. Assert: Verificar que el callback fue llamado
        assertTrue("toListaJuegos debe ser llamado", listaJuegosCalled)
    }

    @Test
    fun adminScreen_clickCerrarSesion_llamaCallback() {
        // 1. Arrange
        var logoutCalled = false
        composeTestRule.setContent {
            Admin(
                toHome = {},
                toAgregarJuego = {},
                toListaJuegos = {},
                onLogout = { logoutCalled = true } // Capturamos la llamada
            )
        }

        // 2. Act: Click en el botón
        composeTestRule.onNodeWithText("Cerrar Sesion").performClick()

        // 3. Assert: Verificar que el callback fue llamado
        assertTrue("onLogout debe ser llamado", logoutCalled)
    }

    @Test
    fun adminScreen_clickVolver_llamaToHomeCallback() {
        // 1. Arrange
        var toHomeCalled = false
        composeTestRule.setContent {
            Admin(
                toHome = { toHomeCalled = true }, // Capturamos la llamada
                toAgregarJuego = {},
                toListaJuegos = {},
                onLogout = {}
            )
        }

        // 2. Act: Click en el NavigationIcon (ArrowBack)
        composeTestRule.onNodeWithContentDescription("Volver").performClick()

        // 3. Assert: Verificar que el callback fue llamado
        assertTrue("toHome debe ser llamado", toHomeCalled)
    }
}