package com.example.desarrollomovil.viewmodels

import com.example.desarrollomovil.data.Item
import com.example.desarrollomovil.data.ItemRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException // Importar una excepción común para simular fallos de red

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val itemRepository: ItemRepository = mockk()
    private lateinit var viewModel: HomeViewModel

    // Nota: Se asume que HomeViewModel tiene una estructura de StateFlows para items, isLoading y error.
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Se asume que HomeViewModel es instanciado con el repositorio mockeado.
        viewModel = HomeViewModel(itemRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // 1. Pruebas de Carga Exitosa
    // -------------------------------------------------------------------------

    @Test
    fun initialState_isCorrect() = runTest {
        // 1. Assert: Verificar el estado inicial
        assertEquals(true, viewModel.items.first().isEmpty())
        assertEquals(false, viewModel.isLoading.first())
        assertEquals(null, viewModel.error.first())
    }

    @Test
    fun loadItems_success_updatesItemsAndLoadingState() = runTest {
        // 1. Arrange: Datos simulados para el repositorio
        val mockItems = listOf(
            Item(id = "1", name = "Producto A", description = "Desc A"),
            Item(id = "2", name = "Servicio B", description = "Desc B")
        )
        // Configurar el mock para retornar datos exitosamente
        coEvery { itemRepository.fetchItems() } returns mockItems

        // 2. Act: Ejecutar la carga
        val initialLoading = viewModel.isLoading.first()
        viewModel.loadItems()
        val finalLoading = viewModel.isLoading.first()
        val actualItems = viewModel.items.first()

        // 3. Assert
        // Verificación de los datos
        assertEquals(2, actualItems.size)
        assertEquals("Producto A", actualItems.first().name)

        // Verificación del estado de carga (debe ser false al finalizar)
        assertEquals(false, finalLoading)
        // Verificación del estado de error (debe ser null)
        assertEquals(null, viewModel.error.first())
    }

    // -------------------------------------------------------------------------
    // 2. Pruebas de Carga con Fallo (Lista Vacía o Lógica Interna)
    // -------------------------------------------------------------------------

    @Test
    fun loadItems_failure_returnsEmptyList_andResetsLoading() = runTest {
        // 1. Arrange: Simular que el repositorio devuelve una lista vacía (ej. base de datos sin datos)
        coEvery { itemRepository.fetchItems() } returns emptyList()

        // 2. Act
        viewModel.loadItems()
        val finalLoading = viewModel.isLoading.first()
        val actualItems = viewModel.items.first()

        // 3. Assert
        // Los datos deben estar vacíos
        assertTrue(actualItems.isEmpty())
        // El estado de carga debe ser false
        assertEquals(false, finalLoading)
        // El estado de error debe ser null
        assertEquals(null, viewModel.error.first())
    }

    // -------------------------------------------------------------------------
    // 3. Pruebas de Manejo de Errores (Excepción del Repositorio)
    // -------------------------------------------------------------------------

    @Test
    fun loadItems_throwsException_setsErrorState() = runTest {
        // 1. Arrange: Simular que el repositorio lanza una excepción (ej. fallo de red)
        val errorMessage = "Error de conexión al servidor"
        coEvery { itemRepository.fetchItems() } throws IOException(errorMessage)

        // 2. Act
        viewModel.loadItems()
        val finalLoading = viewModel.isLoading.first()
        val actualError = viewModel.error.first()

        // 3. Assert
        // El estado de error debe capturar el mensaje
        assertNotNull(actualError)
        assertEquals(errorMessage, actualError)
        // La lista de datos debe seguir vacía
        assertTrue(viewModel.items.first().isEmpty())
        // El estado de carga debe ser false
        assertEquals(false, finalLoading)
    }
}