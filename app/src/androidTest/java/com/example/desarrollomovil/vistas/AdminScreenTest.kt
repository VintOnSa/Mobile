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
            AdminScreen(
                onManageUsersClick = {},
                onManageItemsClick = {}
            )
        }

        // 2. Assert: Verificar la presencia de elementos clave de administración
        composeTestRule.onNodeWithText("Panel de Administración").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gestionar Usuarios").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Gestionar Items").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Reportes del Sistema").assertIsDisplayed()
    }

    @Test
    fun adminScreen_clickGestionarUsuarios_llamaCallback() {
        // 1. Arrange
        var manageUsersCalled = false
        composeTestRule.setContent {
            AdminScreen(
                onManageUsersClick = { manageUsersCalled = true },
                onManageItemsClick = {}
            )
        }

        // 2. Act: Click en el botón de gestión de usuarios
        composeTestRule.onNodeWithText("Gestionar Usuarios").performClick()

        // 3. Assert: Verificar que el callback fue llamado
        assertTrue("onManageUsersClick debe ser llamado", manageUsersCalled)
    }

    @Test
    fun adminScreen_clickGestionarItems_llamaCallback() {
        // 1. Arrange
        var manageItemsCalled = false
        composeTestRule.setContent {
            AdminScreen(
                onManageUsersClick = {},
                onManageItemsClick = { manageItemsCalled = true }
            )
        }

        // 2. Act: Click en el botón de gestión de ítems
        composeTestRule.onNodeWithText("Gestionar Items").performClick()

        // 3. Assert: Verificar que el callback fue llamado
        assertTrue("onManageItemsClick debe ser llamado", manageItemsCalled)
    }
}