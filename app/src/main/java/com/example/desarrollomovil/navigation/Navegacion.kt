package com.example.desarrollomovil.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.desarrollomovil.vistas.*
import com.example.desarrollomovil.vistas.crud.*

@Composable
fun Navegacion() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    NavHost(
        navController = navController,
        startDestination = getStartDestination(sharedPref)
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { usuario ->
                    sharedPref.edit()
                        .putBoolean("is_logged_in", true)
                        .putString("user_type", usuario.tipo)
                        .putString("user_email", usuario.correo)
                        .putString("user_name", usuario.nombre)
                        .apply()
                    navController.navigate("home/${usuario.tipo}") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                toRegistro = {
                    navController.navigate("registro")
                }
            )
        }

        composable("registro") {
            Registro(
                onRegistroExitoso = {
                    navController.navigate("login") {
                        popUpTo("registro") { inclusive = true }
                    }
                },
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        composable("home/{userType}") { backStackEntry ->
            val userType = backStackEntry.arguments?.getString("userType") ?: ""
            val savedUserType = sharedPref.getString("user_type", "") ?: ""
                Home(
                    userType = userType,
                    onProfileAction = {
                        println("DEBUG: Botón presionado, userType: $userType")
                        when (userType.ifEmpty { savedUserType }) {
                            "usuario" -> {
                                println("DEBUG: Navegando a usuario")
                                navController.navigate("usuario")
                            }
                            "admin" -> {
                                println("DEBUG: Navegando a admin")
                                navController.navigate("admin")
                            }
                            else -> {
                                println("DEBUG: Sin Usertype")
                                navController.navigate("login")
                            }
                        }
                    },
                    onLogout = {
                        logout(sharedPref, navController)
                    },
                    onClickJuego = { juegoId ->
                        navController.navigate("verJuego/$juegoId")
                    }
                )
        }

        composable("admin") {
            val currentUserType = sharedPref.getString("user_type", "") ?: ""

            if (currentUserType != "admin") {
                LaunchedEffect(Unit) {
                    navController.navigate("home/$currentUserType") {
                        popUpTo("admin") { inclusive = true }
                    }
                }
            }

            Admin(
                toHome ={
                  navController.navigate("home/admin")
                },
                toAgregarJuego = {
                    navController.navigate("agregarJuego")
                },
                toListaJuegos = { navController.navigate("listaJuegos")
                },
                onLogout = {
                    logout(sharedPref, navController)
                }
            )
        }

        composable("agregarJuego") {
            val currentUserType = sharedPref.getString("user_type", "") ?: ""

            if (currentUserType != "admin") {
                LaunchedEffect(Unit) {
                    navController.navigate("home/$currentUserType") {
                        popUpTo("agregarJuego") { inclusive = true }
                    }
                }
            }

            AgregarJuegoScreen(
                onAgregarSuccess = {
                  navController.navigate("admin")
                },
                onBack = {
                    navController.navigate("admin")
                }
            )
        }
        composable("listaJuegos") {
            val currentUserType = sharedPref.getString("user_type", "") ?: ""

            if (currentUserType != "admin") {
                LaunchedEffect(Unit) {
                    navController.navigate("home/$currentUserType") {
                        popUpTo("listaJuego") { inclusive = true }
                    }
                }
            }
            ListaJuegosScreen(
                onBack = {
                    navController.navigate("admin")
                },
                onEditarJuego = { juegoId ->
                    navController.navigate("editarJuego/$juegoId")
                }
            )
        }

        composable("editarJuego/{juegoId}") { backStackEntry ->
            val juegoId = backStackEntry.arguments?.getString("juegoId")?.toLongOrNull() ?: 0L
            val currentUserType = sharedPref.getString("user_type", "") ?: ""

            if (currentUserType != "admin") {
                LaunchedEffect(Unit) {
                    navController.navigate("home/$currentUserType") {
                        popUpTo("editarJuego/{juegoId}") { inclusive = true }
                    }
                }
            }
            EditarJuegoScreen(
                juegoId = juegoId,
                onBack = {
                    navController.navigate("listaJuegos")
                },
                onEditarSuccess = {
                    navController.navigate("listaJuegos") {
                        popUpTo("editarJuego/{juegoId}") { inclusive = true }
                    }
                }
            )
        }


        composable("verJuego/{juegoId}") { backStackEntry ->
            val juegoId = backStackEntry.arguments?.getString("juegoId")?.toLongOrNull() ?: 0L

            VerJuegoScreen(
                juegoId = juegoId,
                onBack = {
                    navController.navigate("home/")
                }
            )
        }


        composable("usuario") {
            val currentUserType = sharedPref.getString("user_type", "") ?: ""
            val userName = sharedPref.getString("user_name", "") ?: ""

            if (currentUserType != "usuario") {
                LaunchedEffect(Unit) {
                    navController.navigate("home/$currentUserType") {
                        popUpTo("usuario") { inclusive = true }
                    }
                }
            }

            Usuario(
                userName = userName,
                toHome = {
                    navController.navigate("home/usuario")
                },
                onLogout = {
                    logout(sharedPref, navController)
                }
            )
        }
    }
}

private fun getStartDestination(sharedPref: android.content.SharedPreferences): String {
    return if (isUserLoggedIn(sharedPref)) {
        val userType = sharedPref.getString("user_type", "") ?: ""
        "home/$userType"
    } else {
        "home/"
    }
}

private fun isUserLoggedIn(sharedPref: android.content.SharedPreferences): Boolean {
    return sharedPref.getBoolean("is_logged_in", false)
}

private fun logout(sharedPref: android.content.SharedPreferences, navController: androidx.navigation.NavHostController) {
    sharedPref.edit()
        .clear()
        .apply()
    navController.navigate("login") {
        popUpTo(0) { inclusive = true }
    }
}