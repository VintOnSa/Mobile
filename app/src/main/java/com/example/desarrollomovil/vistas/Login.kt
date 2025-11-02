package com.example.desarrollomovil.vistas

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.desarrollomovil.data.*
import com.example.desarrollomovil.ui.theme.DesarrolloMovilTheme
import com.example.desarrollomovil.viewmodels.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (User) -> Unit = {},
                toRegistro: () -> Unit = {})
{

    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = LibreriaDatabase.getDatabase(context)
    val repository = UserRepository(database.userDao())
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(repository)
    )

    var showUserNotRegistered by remember { mutableStateOf(false) }
    var showWrongPassword by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val database = LibreriaDatabase.getDatabase(context)
        val repository = UserRepository(database.userDao())
        repository.initializeAdmin()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Icono",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 50.dp)
        )

        OutlinedTextField(
            value = user,
            onValueChange = {
                user = it
                showUserNotRegistered = false
                showWrongPassword = false
            },
            label = { Text("Usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = pass,
            onValueChange = {
                pass = it
                showUserNotRegistered = false
                showWrongPassword = false
            },
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        val mensaje by userViewModel.mensaje.collectAsState()

        if (!mensaje.isNullOrEmpty()) {
            Text(
                text = mensaje!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        if (showUserNotRegistered) {
            Text(
                text = "Usuario no registrado. Por favor, regístrate primero.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        if (showWrongPassword) {
            Text(
                text = "Contraseña incorrecta. Inténtalo de nuevo.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        Button(
            onClick = {
                userViewModel.limpiarMensaje()
                showUserNotRegistered = false
                showWrongPassword = false
                scope.launch {
                    val usuario = userViewModel.login(user, pass)
                    if (usuario != null) {
                        onLoginSuccess(usuario)
                    } else {
                        val usuarioExiste = userViewModel.verificarUsuarioExiste(user)
                        if (usuarioExiste) {
                            showWrongPassword = true
                        } else {
                            showUserNotRegistered = true
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = user.isNotEmpty() && pass.isNotEmpty()
        ) {
            Text("Iniciar Sesión", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = toRegistro,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Registro", style = MaterialTheme.typography.bodyLarge)
        }

        Text(
            text = "Cuentas de prueba:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    user = "usuario@mail.com"
                    pass = "user123"
                    showUserNotRegistered = false
                    showWrongPassword = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Usuario")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    user = "admin@mail.com"
                    pass = "admin123"
                    showUserNotRegistered = false
                    showWrongPassword = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Admin")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    DesarrolloMovilTheme {
        LoginScreen()
    }
}