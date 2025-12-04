package com.example.desarrollomovil.vistas

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
// ✅ CORRECCIÓN 1: Importar la clase 'Juego' en lugar de 'Item'
import com.example.desarrollomovil.data.Juego
// ✅ CORRECCIÓN 2: Asumimos que HomeViewModel trabaja con el repositorio de Juegos/ítems.
import com.example.desarrollomovil.viewmodels.HomeViewModel
import com.example.desarrollomovil.vistas.HomeScreen // Importación del Composable HomeScreen
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
    // Se usa 'Juego' como tipo de dato en el flujo
    private val itemsFlow = MutableStateFlow<List<Juego>>(emptyList())

    @Before
    fun setup() {
        // Configurar el mock para que el ViewModel emita la lista de juegos
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
        val mockGames = listOf(
            // ✅ CORRECCIÓN 3: Usar la clase Juego y el campo 'titulo' para el texto
            // Se asume que Juego tiene 'id' (Int) y 'titulo' (String) como mínimo
            Juego(id = 42, titulo = "Juego Test", precio = 60.0, stock = 1)
        )
        itemsFlow.value = mockGames // Emitir los datos

        composeTestRule.setContent {
            HomeScreen(
                userType = "usuario",
                // La navegación toma el ID como String, por lo que convertimos 42.toString()
                onNavigateToDetail = { itemId -> itemIdClickedSlot.add(itemId) },
                homeViewModel = mockViewModel
            )
        }

        // 2. Act: Click en el nombre del item (ahora "Juego Test")
        composeTestRule.onNodeWithText("Juego Test").performClick()

        // 3. Assert: Verificar que el callback se llamó con el ID correcto (como String)
        assertEquals(1, itemIdClickedSlot.size)
        // El ID debe ser "42" ya que la navegación usa Strings
        assertEquals("42", itemIdClickedSlot.first())
    }
}