package com.example.desarrollomovil.vistas

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.desarrollomovil.data.Juego
import com.example.desarrollomovil.data.JuegoRepository
import com.example.desarrollomovil.data.LibreriaDatabase
import com.example.desarrollomovil.viewmodels.JuegoViewModel
import com.example.desarrollomovil.viewmodels.JuegoViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerJuegoScreen(
    juegoId: Long,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val database = LibreriaDatabase.getDatabase(context)
    val repository = JuegoRepository(database.juegoDao())
    val juegoViewModel: JuegoViewModel = viewModel(factory = JuegoViewModelFactory(repository))

    var juegoSeleccionado by remember { mutableStateOf<Juego?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showBuyAlert by remember { mutableStateOf(false) }
    var juegoAComprar by remember { mutableStateOf<Juego?>(null) }


    //Toast cuadno agreges al carro por ahora no hace nada
    //LaunchedEffect(juegoViewModel.mensaje) {
    //  juegoViewModel.mensaje.collect { mensaje ->
    //    if (mensaje != null && mensaje.contains("exito")) {
    //        Toast.makeText(context, "Juego Añadido", Toast.LENGTH_SHORT).show()
    //   }
    // }
    //}

    LaunchedEffect(juegoId) {
        isLoading = true
        try {
            val juego = juegoViewModel.obtenerJuegoPorId(juegoId)
            juegoSeleccionado = juego
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar juego", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }


    if (showBuyAlert && juegoAComprar != null) {
        AlertDialog(
            onDismissRequest = {
                showBuyAlert = false
                juegoAComprar = null
            },
            title = { Text("Añadir al Carro") },
            text = {
                Text("¿Estás seguro de que quieres Añadir \"${juegoAComprar!!.titulo}\" al Carro de Compras?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        juegoViewModel.eliminarJuego(juegoAComprar!!.id)
                        showBuyAlert = false
                        juegoAComprar = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Añadir")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showBuyAlert = false
                        juegoAComprar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles Juego") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cargando juego...")
                    }
            }
            juegoSeleccionado == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Juego no encontrado")
                }
            } else -> {
                CenteredColumnWithSpacing(10.dp, paddingValues) {
                    AsyncImage(
                        model = juegoSeleccionado?.imagenUri,
                        contentDescription = "Portada de ${juegoSeleccionado?.titulo}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )


                    Text(
                        text = "${juegoSeleccionado?.titulo}",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Publicador: ${juegoSeleccionado?.publicador}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Genero: ${juegoSeleccionado?.genero}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Plataforma: ${juegoSeleccionado?.plataforma}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${juegoSeleccionado?.descripcion}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Stock: ${juegoSeleccionado?.stock} Unidades Disponibles",
                        style = MaterialTheme.typography.bodyMedium
                    )


                    Text(
                        text = "$${juegoSeleccionado?.precio?.toInt()}",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                juegoAComprar = juegoSeleccionado
                                showBuyAlert = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.ShoppingCart, "Comprar")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Comprar")
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun VerJuegoPreview() {
    VerJuegoScreen(juegoId = 1L)
}