package com.example.desarrollomovil.vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.desarrollomovil.ui.theme.DesarrolloMovilTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Usuario(userName: String, toHome: () -> Unit = {}, onLogout: () -> Unit = {}, onBack: () -> Unit = {}, toPedidos: () -> Unit = {}) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hola $userName!") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        CenteredColumnWithSpacing(16.dp, paddingValues)
        {
            Button(
                onClick = toPedidos,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Mis Pedidos")
            }
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Cerrar Sesion")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UsuarioPreview() {
    DesarrolloMovilTheme {
        Usuario(
            userName = "Usuario",
            toHome = { },
            onLogout = { },
            toPedidos={})

    }
}

