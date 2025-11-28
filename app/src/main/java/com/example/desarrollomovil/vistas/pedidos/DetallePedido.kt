package com.example.desarrollomovil.vistas.pedidos


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePedidoScreen(
    pedidoId: String = "PED-001",
    onBack: () -> Unit = {},
    onCancelarPedido: () -> Unit = {}
) {
    val pedido = remember(pedidoId) {
        PedidoDetalleFicticio(
            codigo = pedidoId,
            fecha = "15 Nov 2024",
            estado = "En proceso",
            subtotal = 145000.0,
            envio = 4990.0,
            total = 149990.0,
            juegos = listOf(
                JuegoPedido(
                    nombre = "The Legend of Zelda: Breath of the Wild",
                    cantidad = 1,
                    precioUnitario = 59990.0,
                    subtotal = 59990.0
                ),
                JuegoPedido(
                    nombre = "Minecraft",
                    cantidad = 2,
                    precioUnitario = 34990.0,
                    subtotal = 69990.0
                ),
                JuegoPedido(
                    nombre = "Super Mario Odyssey",
                    cantidad = 1,
                    precioUnitario = 49990.0,
                    subtotal = 49990.0
                )
            )
        )
    }

    var showCancelDialog by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancelar Pedido") },
            text = { Text("¿Estás seguro de que quieres cancelar este pedido? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        onCancelarPedido()
                    }
                ) {
                    Text("Cancelar Pedido", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCancelDialog = false }
                ) {
                    Text("Mantener Pedido")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Pedido") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        },
        bottomBar = {
            if (pedido.estado == "En proceso") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    tonalElevation = 8.dp,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Button(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Icon(Icons.Default.Delete, "Cancelar")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cancelar Pedido", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Información del Pedido",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        InfoRow("Código:", pedido.codigo)
                        InfoRow("Fecha:", pedido.fecha)
                        InfoRow("Estado:", pedido.estado)
                    }
                }
            }

            item {
                Text(
                    "Juegos en el Pedido",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(pedido.juegos) { juego ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            juego.nombre,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Cantidad: ${juego.cantidad}")
                            Text("Precio: $${juego.precioUnitario}")
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal:")
                            Text(
                                "$${juego.subtotal}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Resumen de Pago",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PrecioRow("Subtotal:", pedido.subtotal)
                        PrecioRow("Envío:", pedido.envio)

                        Spacer(modifier = Modifier.height(8.dp))

                        Divider()

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "TOTAL:",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "$${String.format("%.0f", pedido.total)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value)
    }
}

@Composable
fun PrecioRow(label: String, valor: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text("$${String.format("%.2f", valor)}")
    }
}

data class PedidoDetalleFicticio(
    val codigo: String,
    val fecha: String,
    val estado: String,
    val subtotal: Double,
    val envio: Double,
    val total: Double,
    val juegos: List<JuegoPedido>
)

data class JuegoPedido(
    val nombre: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)

@Preview(showBackground = true)
@Composable
fun DetallePedidoScreenPreview() {
    MaterialTheme {
        DetallePedidoScreen()
    }
}