package com.example.desarrollomovil.vistas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.desarrollomovil.viewmodels.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(
    onRegistroExitoso: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val database = LibreriaDatabase.getDatabase(context)
    val repository = UserRepository(database.userDao())
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(repository)
    )
    val mensaje by userViewModel.mensaje.collectAsState()

    LaunchedEffect(mensaje) {
        if (mensaje != null && mensaje!!.contains("exito")) {
            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_LONG).show()
            nombre = ""; correo = ""; password = ""; confirmPassword = "";
            onRegistroExitoso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        CenteredColumnWithSpacing( 16.dp, paddingValues)
            {
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre completo *") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = correo, onValueChange = { correo = it },
                label = { Text("Correo electrónico *") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Contraseña *") }, modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña *") }, modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            if (!mensaje.isNullOrEmpty()) {
                Text(
                    text = mensaje!!,
                    color = if (mensaje!!.contains("exito")) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Button(
                onClick = {
                    userViewModel.limpiarMensaje()
                    if (password != confirmPassword) {
                        return@Button
                    }
                    userViewModel.registrarUsuario(nombre, correo, password)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Registrarse")
            }
        }
    }
}
@Preview
@Composable
fun RegistroScreenPreview() {
    Registro()
}

