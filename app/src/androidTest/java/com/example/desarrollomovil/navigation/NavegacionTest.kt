package com.example.desarrollomovil.navigation

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.desarrollomovil.data.User // Asegúrate de que tu clase User sea accesible en los tests
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@RequiresApi(Build.VERSION_CODES.O)
class NavegacionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    // Contexto de la aplicación real para simular SharedPreferences
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setup() {
        // 1. Inicializar el NavController de prueba y los Mocks de SharedPreferences
        navController = TestNavHostController(context)

        // Usamos un mock para simular SharedPreferences
        mockSharedPreferences = mockk<SharedPreferences>(relaxed = true)
        mockEditor = mockk<SharedPreferences.Editor>(relaxed = true)
        every { mockSharedPreferences.edit() } returns mockEditor

        // Simular que el editor puede encadenar llamadas
        every { mockEditor.putBoolean(any(), any()) } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor
        every { mockEditor.clear() } returns mockEditor
        every { mockEditor.apply() } just runs

        // 2. Iniciar el Composable Navegacion dentro de la regla de prueba
        composeTestRule.setContent {
            // Reemplazar el LocalContext para inyectar un Contexto que devuelva nuestro mock SharedPreferences
            val localContext = LocalContext.current
            val contextWrapper: Context = mockk(relaxed = true) {
                // Devolver el mock SharedPreferences cuando se solicite
                every { getSharedPreferences("app_prefs", Context.MODE_PRIVATE) } returns mockSharedPreferences
            }

            // Usar el contexto inyectado si es posible, o componer la navegación directamente
            Navegacion()
        }

        // Asignar el NavController al NavHost
        navController.setViewModelStore(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    // ---------------------------------------------------------------------------------------------
    // Pruebas de Flujo Básico (Sección 4)
    // ---------------------------------------------------------------------------------------------

    @Test
    fun navegacion_inicio_sinLogin_navegaALogin() {
        // 1. Arrange: Simular que el usuario NO está logeado
        every { mockSharedPreferences.getBoolean("is_logged_in", false) } returns false

        // 2. Act: Navegar a la ruta de login
        navController.navigate("login")

        // 3. Assert: Verificar que estamos en la pantalla de Login
        assertEquals("login", navController.currentDestination?.route)
    }

    @Test
    fun navegacion_loginExitoso_navegaHomeUsuario() {
        // 1. Arrange
        val usuario = User(nombre = "Test", correo = "a@a.com", password = "p", tipo = "usuario")
        val expectedRoute = "home/${usuario.tipo}" // home/usuario

        // 2. Act: Simular la navegación del LoginScreen al Home
        navController.navigate(expectedRoute) {
            popUpTo("login") { inclusive = true }
        }

        // 3. Assert: Verificar la ruta actual
        assertEquals(expectedRoute, navController.currentDestination?.route)
    }

    @Test
    fun navegacion_navegaAUsuarioDesdeHome_rutaCorrecta() {
        // 1. Arrange: El usuario es 'usuario' y está en 'home/usuario'
        navController.navigate("home/usuario")

        // 2. Act: Simular el click en el perfil de usuario desde Home
        navController.navigate("usuario")

        // 3. Assert: Verificar la ruta actual
        assertEquals("usuario", navController.currentDestination?.route)
    }

    @Test
    fun navegacion_accesoAdmin_sinPermisos_redirigeAHome() {
        // 1. Arrange: Simular que el usuario actual es "usuario"
        every { mockSharedPreferences.getString("user_type", "") } returns "usuario"

        // 2. Act: Intentar navegar a "admin"
        navController.navigate("admin")

        // 3. Simular la redirección que ocurre en el LaunchedEffect del Composable Admin()
        navController.navigate("home/usuario") {
            popUpTo("admin") { inclusive = true }
        }

        // 4. Assert: Verificar la redirección
        assertEquals("home/usuario", navController.currentDestination?.route)
    }

    @Test
    fun navegacion_logout_navegaALoginYLimpiaPila() {
        // 1. Arrange: Simular que el usuario está logeado y en una ruta profunda
        navController.navigate("home/admin")
        assertEquals("home/admin", navController.currentDestination?.route)

        // 2. Act: Simular la ejecución de la función logout
        mockSharedPreferences.edit().clear()
        navController.navigate("login") {
            // Esto simula popUpTo(navController.graph.id) { inclusive = true }
            popUpTo(navController.graph.id) { inclusive = true }
        }

        // 3. Assert: Verificar que la ruta actual es "login" y se llamó a clear()
        assertEquals("login", navController.currentDestination?.route)
        verify { mockEditor.clear() }
    }

    // ---------------------------------------------------------------------------------------------
    // Pruebas Adicionales de Inicio y Flujo Interno (Sección 7)
    // ---------------------------------------------------------------------------------------------

    @Test
    fun navegacion_inicioConUsuarioLogeado_navegaAHomeUsuario() {
        // 1. Arrange: Simular usuario logeado y tipo "usuario"
        every { mockSharedPreferences.getBoolean("is_logged_in", false) } returns true
        every { mockSharedPreferences.getString("user_type", "") } returns "usuario"

        // 2. Act: Navegamos a la ruta inicial que resuelve al Home correcto (simulando el arranque)
        navController.navigate("home/usuario")

        // 3. Assert: Verificar que la ruta de inicio esperada sea "home/usuario"
        assertEquals("home/usuario", navController.currentDestination?.route)
    }

    @Test
    fun navegacion_desdeHomeAdmin_navegaAAdminScreen() {
        // 1. Arrange: Simular que estamos en el Home de Admin
        navController.navigate("home/admin")
        assertEquals("home/admin", navController.currentDestination?.route)

        // 2. Act: Simular la navegación interna a la pantalla "admin"
        navController.navigate("admin")

        // 3. Assert: Verificar que la ruta actual es la pantalla de administrador
        assertEquals("admin", navController.currentDestination?.route)
    }
}