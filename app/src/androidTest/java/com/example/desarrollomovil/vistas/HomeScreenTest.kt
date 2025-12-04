package com.example.desarrollomovil.vistas

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.desarrollomovil.data.Item
import com.example.desarrollomovil.viewmodels.HomeViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<HomeViewModel>(relaxed = true)
    private val itemsFlow = MutableStateFlow<List<Item>>(emptyList())

    @Before
    fun setup() {
        // Configurar el mock para que el ViewModel emita la lista de ítems
        every { mockViewModel.items } returns itemsFlow
    }

    @Test
    fun homeScreen_usuarioEstandar_muestraSaludoCorrectoYNoAdmin() {
        // 1. Arrange
        val userType = "usuario"
        composeTestRule.setContent {
            HomeScreen(
                userType = userType,
                onNavigateToDetail = {},
                homeViewModel = mockViewModel
            )
        }

        // 2. Assert: Saludo para usuario estándar
        composeTestRule.onNodeWithText("Bienvenido, usuario!").assertIsDisplayed()
        // 3. Assert: No debe ver elementos de administración
        composeTestRule.onNodeWithText("Panel de Administración").assertDoesNotExist()
    }

    @Test
    fun homeScreen_adminUser_muestraSaludoAdmin() {
        // 1. Arrange
        val userType = "admin"
        composeTestRule.setContent {
            HomeScreen(
                userType = userType,
                onNavigateToDetail = {},
                homeViewModel = mockViewModel
            )
        }

        // 2. Assert: Saludo para admin y elementos de administración
        composeTestRule.onNodeWithText("Bienvenido, admin!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Panel de Administración").assertIsDisplayed()
    }

    @Test
    fun homeScreen_clickEnItem_llamaCallbackNavegacion() {
        // 1. Arrange
        val itemIdClickedSlot = mutableListOf<String>()
        val mockItems = listOf(
            Item(id = "42", name = "Item Test", description = "Clickable item")
        )
        itemsFlow.value = mockItems // Emitir los datos

        composeTestRule.setContent {
            HomeScreen(
                userType = "usuario",
                onNavigateToDetail = { itemId -> itemIdClickedSlot.add(itemId) },
                homeViewModel = mockViewModel
            )
        }

        // 2. Act: Click en el nombre del item
        composeTestRule.onNodeWithText("Item Test").performClick()

        // 3. Assert: Verificar que el callback se llamó con el ID correcto
        assertEquals(1, itemIdClickedSlot.size)
        assertEquals("42", itemIdClickedSlot.first())
    }
}