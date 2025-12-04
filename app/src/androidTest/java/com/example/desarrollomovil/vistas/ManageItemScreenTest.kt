package com.example.desarrollomovil.vistas

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.desarrollomovil.data.Item
import com.example.desarrollomovil.viewmodels.HomeViewModel
import io.mockk.*
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Asumiendo una estructura simple para el Composable ManageItemScreen
// que toma un HomeViewModel para manejar la lógica de guardar/actualizar.

@RunWith(AndroidJUnit4::class)
class ManageItemScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<HomeViewModel>(relaxed = true)

    @Test
    fun manageItemScreen_muestraFormularioYBotonGuardar() {
        // 1. Arrange: Cargar la pantalla para crear un nuevo item (sin item para editar)
        composeTestRule.setContent {
            ManageItemScreen(
                itemToEdit = null, // Crear nuevo ítem
                onItemSaved = {},
                homeViewModel = mockViewModel
            )
        }

        // 2. Assert: Verificar la presencia de elementos clave del formulario
        composeTestRule.onNodeWithText("Nuevo Ítem").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nombre del Ítem").assertIsDisplayed()
        composeTestRule.onNodeWithText("Descripción").assertIsDisplayed()
        composeTestRule.onNodeWithText("Guardar").assertIsDisplayed().assertIsNotEnabled() // Inicialmente deshabilitado
    }

    @Test
    fun manageItemScreen_camposValidos_botonGuardarHabilitado() {
        // 1. Arrange
        composeTestRule.setContent {
            ManageItemScreen(itemToEdit = null, onItemSaved = {}, homeViewModel = mockViewModel)
        }

        // 2. Act: Ingresar datos en todos los campos
        composeTestRule.onNodeWithText("Nombre del Ítem").performTextInput("Ítem de Prueba")
        composeTestRule.onNodeWithText("Descripción").performTextInput("Esto es una descripción detallada.")

        // 3. Assert: El botón "Guardar" debe estar habilitado
        composeTestRule.onNodeWithText("Guardar").assertIsEnabled()
    }

    @Test
    fun manageItemScreen_modoEdicion_muestraDatosExistentes() {
        // 1. Arrange: Cargar la pantalla con un ítem para editar
        val item = Item(id = "101", name = "Antiguo Nombre", description = "Antigua Descripción")
        composeTestRule.setContent {
            ManageItemScreen(itemToEdit = item, onItemSaved = {}, homeViewModel = mockViewModel)
        }

        // 2. Assert: Verificar que los TextFields muestran los valores del ítem
        composeTestRule.onNodeWithText("Editar Ítem").assertIsDisplayed()
        composeTestRule.onNodeWithText("Antiguo Nombre").assertIsDisplayed()
        composeTestRule.onNodeWithText("Antigua Descripción").assertIsDisplayed()
        composeTestRule.onNodeWithText("Guardar").assertIsEnabled()
    }

    @Test
    fun manageItemScreen_clickGuardar_llamaAViewModelYCallback() {
        // 1. Arrange
        var itemSavedCalled = false
        val itemCapturadoSlot = slot<Item>()
        val itemToEdit = Item(id = "202", name = "Editado", description = "Original")

        // Simular que el ViewModel guarda el ítem
        coEvery { mockViewModel.saveItem(capture(itemCapturadoSlot)) } returns Unit

        composeTestRule.setContent {
            ManageItemScreen(
                itemToEdit = itemToEdit,
                onItemSaved = { itemSavedCalled = true },
                homeViewModel = mockViewModel
            )
        }

        // Act: Modificar la descripción y guardar
        composeTestRule.onNodeWithText("Original").performTextClearance()
        composeTestRule.onNodeWithText("Original").performTextInput("Descripción Nueva")

        composeTestRule.onNodeWithText("Guardar").performClick()

        // 3. Assert: Verificar la llamada y los datos capturados
        assertTrue("onItemSaved debe ser llamado", itemSavedCalled)
        coVerify(exactly = 1) { mockViewModel.saveItem(any()) }

        // Verificar que los datos enviados al ViewModel son correctos (incluye la edición)
        assertEquals("Descripción Nueva", itemCapturadoSlot.captured.description)
        assertEquals("202", itemCapturadoSlot.captured.id) // El ID se mantiene en modo edición
    }
}