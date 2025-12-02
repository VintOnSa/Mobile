package com.example.desarrollomovil.vistas.crud

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.desarrollomovil.data.Juego
import com.example.desarrollomovil.viewmodels.JuegoViewModel
import com.example.desarrollomovil.vistas.vibrar
import java.text.DecimalFormat

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaJuegosScreen(
    onBack: () -> Unit = {},
    onEditarJuego: (Int) -> Unit = {}
) {
    val context = LocalContext.current

    val juegoViewModel = viewModel<JuegoViewModel>()
    var state = juegoViewModel.state

    var isLoading by remember { mutableStateOf(true) }
    val formato = DecimalFormat("#,###")

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            juegoViewModel.ObtenerJuegos()
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar juegos", Toast.LENGTH_SHORT).show()
        }finally{
            isLoading = false
        }
    }

    var textoBusqueda by remember { mutableStateOf("") }

    var showDeleteAlert by remember { mutableStateOf(false) }
    var juegoAEliminar by remember { mutableStateOf<Juego?>(null) }


    val juegosFiltrados = remember(state.juego, textoBusqueda) {
        if (textoBusqueda.isBlank()) {
            state.juego
        } else {
            state.juego.filter { juego ->
                        juego.titulo.contains(textoBusqueda, ignoreCase = true) ||
                        juego.publicador.contains(textoBusqueda, ignoreCase = true) ||
                        juego.genero.contains(textoBusqueda, ignoreCase = true)
            }
        }
    }

    if (showDeleteAlert && juegoAEliminar != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteAlert = false
                juegoAEliminar = null
            },
            title = { Text("Confirmar Eliminacion") },
            text = {
                Text("¿Estás seguro de que quieres eliminar \"${juegoAEliminar!!.titulo}\"? Esta accion no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        juegoViewModel.eliminarJuego(juegoAEliminar!!.id)
                        showDeleteAlert = false
                        juegoAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteAlert = false
                        juegoAEliminar = null
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
                title = { Text("Lista de Juegos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text("Cargando juegos...",
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    label = { Text("Buscar juego") },
                    placeholder = { Text("Buscar") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, "Buscar")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true
                )

                if (juegosFiltrados.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (juegosFiltrados.isEmpty()) {
                            Text("No hay juegos disponibles")
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No se encontraron juegos", style = MaterialTheme.typography.bodyLarge)
                                Text("\"$textoBusqueda\"", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { textoBusqueda = "" }) {
                                    Text("Limpiar barra de busqueda")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(juegosFiltrados) { juego ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    AsyncImage(
                                        model = juego.imagenurl,
                                        contentDescription = "Portada de ${juego.titulo}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = juego.titulo,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Text(
                                        text = "Publicador: ${juego.publicador}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Genero: ${juego.genero}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Text(
                                        text = "Precio: $${formato.format(juego.precio)}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Text(
                                        text = "Stock: ${juego.stock}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Button(
                                            onClick = { onEditarJuego(juego.id) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Icon(Icons.Default.Edit, "Editar")
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Editar")
                                        }

                                        Button(
                                            onClick = {
                                                vibrar(context)
                                                juegoAEliminar = juego
                                                showDeleteAlert = true
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Icon(Icons.Default.Delete, "Eliminar")
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Eliminar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun ListaJuegosScreenPreview() {
    ListaJuegosScreen()
}