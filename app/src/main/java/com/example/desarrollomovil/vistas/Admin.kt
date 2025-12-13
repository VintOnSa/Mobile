package com.example.desarrollomovil.vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.desarrollomovil.ui.theme.DesarrolloMovilTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Admin(
    toHome: () -> Unit = {},
    toAgregarJuego: () -> Unit = {},
    toListaJuegos: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administracion") },
                navigationIcon = {
                    IconButton(onClick = toHome) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        CenteredColumnWithSpacing( 16.dp, paddingValues){

            Button(
                onClick = toAgregarJuego,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)

            ) {
                Text("Agregar Juego")
            }
            Button(
                onClick = toListaJuegos,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Lista de Juegos")
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
fun AdminPreview() {
    DesarrolloMovilTheme {
        Admin(
            toHome = {},
            toAgregarJuego = {},
            toListaJuegos = {},
            onLogout = {})
    }
}
