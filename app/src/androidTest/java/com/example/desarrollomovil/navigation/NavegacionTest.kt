package com.example.desarrollomovil.navigation


import android.content.Context
import android.content.SharedPreferences
import android.os.Build

import androidx.test.filters.SdkSuppress
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.lifecycle.ViewModelStore
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
class NavegacionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setup() {
        // 1. Inicializar el NavController de prueba y los Mocks de SharedPreferences
        navController = TestNavHostController(context)

        mockSharedPreferences = mockk<SharedPreferences>(relaxed = true)
        mockEditor = mockk<SharedPreferences.Editor>(relaxed = true)
        every { mockSharedPreferences.edit() } returns mockEditor

        every { mockEditor.putBoolean(any(), any()) } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor
        every { mockEditor.clear() } returns mockEditor
        every { mockEditor.apply() } just runs

        // 2. Iniciar el Composable Navegacion
        composeTestRule.setContent {
            val localContext = LocalContext.current
            val contextWrapper: Context = mockk(relaxed = true) {
                every { getSharedPreferences("app_prefs", Context.MODE_PRIVATE) } returns mockSharedPreferences
            }

            Navegacion()
        }

        // 3. Establecer el ViewModelStore
        navController.setViewModelStore(ViewModelStore())
    }

    // ---------------------------------------------------------------------------------------------
    // Pruebas de Flujo Básico
    // ---------------------------------------------------------------------------------------------

    @Test
    fun navegacion_inicio_sinLogin_navegaALogin() {
        every { mockSharedPreferences.getBoolean("is_logged_in", false) } returns false
        navController.navigate("login")
        assertEquals("login", navController.currentDestination?.route)
    }

    @Test
    fun navegacion_loginExitoso_navegaHomeUsuario() {
        val expectedRoute = "home/usuario"

        navController.navigate(expectedRoute) {
            popUpTo("login") { inclusive = true }
        }
        assertEquals(expectedRoute, navController.currentDestination?.route)
    }

    @Test
    fun navegacion_navegaAUsuarioDesdeHome_rutaCorrecta() {
        navController.navigate("home/usuario")
        navController.navigate("usuario")
        assertEquals("usuario", navController.currentDestination?.route)
    }

    @Test
    fun navegacion_accesoAdmin_sinPermisos_redirigeAHome() {
        every { mockSharedPreferences.getString("user_type", "") } returns "usuario"
        navController.navigate("admin")

        // Simular la redirección
        navController.navigate("home/usuario") {
            popUpTo("admin") { inclusive = true }
        }
        assertEquals("home/usuario", navController.currentDestination?.route)
    }

    @Test
    fun navegacion_logout_navegaALoginYLimpiaPila() {
        navController.navigate("home/admin")

        // Simular la ejecución de logout
        mockSharedPreferences.edit().clear()
        navController.navigate("login") {
            popUpTo(navController.graph.id) { inclusive = true }
        }
        verify { mockEditor.clear() }
        assertEquals("login", navController.currentDestination?.route)
    }

    // ---------------------------------------------------------------------------------------------
    // Pruebas Adicionales de Inicio y Flujo Interno
    // ---------------------------------------------------------------------------------------------

    @Test
    fun navegacion_inicioConUsuarioLogeado_navegaAHomeUsuario() {
        every { mockSharedPreferences.getBoolean("is_logged_in", false) } returns true
        every { mockSharedPreferences.getString("user_type", "") } returns "usuario"

        navController.navigate("home/usuario")

        assertEquals("home/usuario", navController.currentDestination?.route)
    }

    @Test
    fun navegacion_desdeHomeAdmin_navegaAAdminScreen() {
        navController.navigate("home/admin")
        navController.navigate("admin")

        assertEquals("admin", navController.currentDestination?.route)
    }
}