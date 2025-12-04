package com.example.desarrollomovil.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.desarrollomovil.repository.JuegoService // Importación del objeto estático
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import okhttp3.MultipartBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException

// --- ESTRUCTURAS DE DATOS MÍNIMAS SIMULADAS ---
// Deben coincidir con tu código de producción
data class Juego(
    val id: Int = 0, val titulo: String = "", val publicador: String = "",
    val precio: Int = 0, val stock: Int = 0, val descripcion: String = "",
    val plataforma: String = "", val genero: String = "", val imagenurl: String? = null
)
data class JuegoAgregar(
    val titulo: String, val publicador: String, val precio: Int, val stock: Int,
    val descripcion: String, val plataforma: String, val genero: String, val imagenurl: String?
)
data class ImagenUploadResponse(val imageUrl: String)
// Asumimos un JuegoState mínimo para el formulario
data class JuegoState(
    val id: Int = 0, val titulo: String = "", val publicador: String = "",
    val precio: String = "", val stock: String = "", val descripcion: String = "",
    val plataforma: String = "", val genero: String = "", val imagenurl: String? = null,
    val juego: List<Juego> = emptyList() // Lista de juegos para la pantalla
)

// Interfaz para mockear los métodos del servicio API
interface MockJuegosService {
    suspend fun obtenerJuegos(): List<Juego>
    suspend fun subirImagen(imagen: MultipartBody.Part): ImagenUploadResponse
    suspend fun agregarJuego(juego: JuegoAgregar)
    suspend fun buscarJuego(id: Int): Juego
    suspend fun actualizarJuego(juego: Juego)
    suspend fun eliminarJuego(id: Int)
}
// --------------------------------------------------

@ExperimentalCoroutinesApi
class JuegoViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    // Mock de la instancia del servicio real (necesita ser del tipo de la interfaz)
    private val mockJuegosService: MockJuegosService = mockk()
    private lateinit var viewModel: JuegoViewModel

    // Inicializar los mocks y el entorno de corrutinas
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // 💡 CLAVE: Mockear el objeto estático JuegoService y configurar su propiedad 'instance'
        mockkObject(JuegoService)
        // Usamos el casting 'as JuegoService' para inyectar nuestro mock en la propiedad estática
        every { JuegoService.instance } returns (mockJuegosService as JuegoService)

        // El ViewModel se inicializará y llamará a ObtenerJuegos()
        viewModel = JuegoViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        // Limpiar el mock del objeto estático al final de cada test
        unmockkObject(JuegoService)
    }

    // -------------------------------------------------------------------------
    // 1. Pruebas de Inicialización y Carga (ObtenerJuegos)
    // -------------------------------------------------------------------------

    @Test
    fun init_loadsJuegos_andUpdatesState() = runTest {
        // 1. Arrange: Configurar el mock para que devuelva datos
        val juegosSimulados = listOf(Juego(id = 1, titulo = "Juego Demo", precio = 10))
        coEvery { mockJuegosService.obtenerJuegos() } returns juegosSimulados

        // 2. Act: Esperar a que el launch de init{} termine
        testScheduler.advanceUntilIdle()

        // 3. Assert: Verificar la lista en el estado
        assertEquals(1, viewModel.state.juego.size)
        assertEquals("Juego Demo", viewModel.state.juego.first().titulo)
    }

    @Test
    fun obtenerJuegos_failure_setsErrorMessage() = runTest {
        // 1. Arrange: Simular una excepción
        val errorMessage = "Failed to connect"
        coEvery { mockJuegosService.obtenerJuegos() } throws IOException(errorMessage)

        // 2. Act: Llamar a la función y esperar
        viewModel.ObtenerJuegos()
        testScheduler.advanceUntilIdle()

        // 3. Assert: Verificar el mensaje de error
        val mensaje = viewModel.mensaje.first()
        assertTrue(viewModel.state.juego.isEmpty())
        assertTrue(mensaje!!.contains("No se encontraron Juegos"))
    }

    // -------------------------------------------------------------------------
    // 2. Pruebas de Manipulación de Estado de Formulario (Setters)
    // -------------------------------------------------------------------------

    @Test
    fun cTitulo_updatesComposeState() {
        viewModel.cTitulo("Nuevo Título de Test")
        assertEquals("Nuevo Título de Test", viewModel.state.titulo)
    }

    @Test
    fun cPrecio_updatesComposeState() {
        viewModel.cPrecio("99")
        assertEquals("99", viewModel.state.precio)
    }

    // -------------------------------------------------------------------------
    // 3. Pruebas de Agregar Juego (Con y Sin Archivo)
    // -------------------------------------------------------------------------

    @Test
    fun agregarJuego_withoutImage_callsServiceAndResetsState() = runTest {
        // 1. Arrange: Llenar el estado del formulario del VM
        viewModel.cTitulo("Nuevo Juego Sin Imagen")
        viewModel.cPrecio("50")
        viewModel.cStock("10")

        // Mockear el servicio API
        coEvery { mockJuegosService.agregarJuego(any()) } just runs

        // 2. Act
        viewModel.agregarJuego(imagenFile = null)
        testScheduler.advanceUntilIdle()

        // 3. Assert: Verificar llamadas, mensaje y reseteo
        coVerify(exactly = 0) { mockJuegosService.subirImagen(any()) } // No se sube imagen
        coVerify(exactly = 1) { mockJuegosService.agregarJuego(match { it.titulo == "Nuevo Juego Sin Imagen" }) }
        assertEquals("Juego agregado exitosamente", viewModel.mensaje.first())

        // Verificar que el estado del formulario se limpió
        assertEquals("", viewModel.state.titulo)
    }

    // Para probar la subida de imagen, necesitarías un mock más avanzado para el objeto java.io.File
    // y para MultipartBody.Part, lo cual es complejo en un test unitario simple.
    // Omitimos esta prueba específica por la complejidad de mocking de clases Java/OkHttp.


    // -------------------------------------------------------------------------
    // 4. Pruebas de Obtener Juego por ID (Para Edición)
    // -------------------------------------------------------------------------

    @Test
    fun obtenerJuegoPorId_success_updatesFormState() = runTest {
        // 1. Arrange
        val juegoEncontrado = Juego(id = 5, titulo = "Juego para Editar", precio = 90, stock = 5)
        coEvery { mockJuegosService.buscarJuego(5) } returns juegoEncontrado

        // 2. Act
        viewModel.obtenerJuegoPorId(5)
        testScheduler.advanceUntilIdle()

        // 3. Assert: Verificar que el estado del formulario se cargó
        assertEquals("Juego para Editar", viewModel.state.titulo)
        assertEquals("90", viewModel.state.precio) // Debe ser String en el estado
        coVerify(exactly = 1) { mockJuegosService.buscarJuego(5) }
    }

    // -------------------------------------------------------------------------
    // 5. Pruebas de Eliminación
    // -------------------------------------------------------------------------

    @Test
    fun eliminarJuego_success_callsServiceAndRefreshes() = runTest {
        val juegoId = 7

        // 1. Arrange: Mockear ambas llamadas (eliminar y obtenerJuegos para refresh)
        coEvery { mockJuegosService.eliminarJuego(juegoId) } just runs
        coEvery { mockJuegosService.obtenerJuegos() } returns emptyList() // Refresh

        // 2. Act
        viewModel.eliminarJuego(juegoId)
        testScheduler.advanceUntilIdle()

        // 3. Assert: Verificar llamadas y mensaje
        coVerify(exactly = 1) { mockJuegosService.eliminarJuego(juegoId) }
        // Verificar que ObtenerJuegos fue llamado para refrescar (llamada de init + llamada de refresh)
        coVerify(exactly = 2) { mockJuegosService.obtenerJuegos() }
        assertEquals("Juego eliminado exitosamente", viewModel.mensaje.first())
    }
}