package com.example.desarrollomovil.vistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    onBack: () -> Unit = {},
    toDetallePedido: (String) -> Unit = {}
) {
    val pedidos = listOf(
        PedidoFicticio(
            codigo = "PED-001",
            fecha = "15 Nov 2024",
            precioTotal = 149990.0,
            estado = "Completado"
        ),
        PedidoFicticio(
            codigo = "PED-002",
            fecha = "10 Nov 2024",
            precioTotal = 89990.0,
            estado = "En proceso"
        ),
        PedidoFicticio(
            codigo = "PED-003",
            fecha = "05 Nov 2024",
            precioTotal = 59990.0,
            estado = "Completado"
        ),
        PedidoFicticio(
            codigo = "PED-004",
            fecha = "01 Nov 2024",
            precioTotal = 129990.0,
            estado = "Cancelado"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (pedidos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            "Sin pedidos",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No tienes pedidos realizados",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pedidos) { pedido ->
                        Card(
                            onClick = { toDetallePedido(pedido.codigo) },
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Código: ${pedido.codigo}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        pedido.estado,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = when (pedido.estado) {
                                            "Completado" -> MaterialTheme.colorScheme.primary
                                            "En proceso" -> MaterialTheme.colorScheme.secondary
                                            "Cancelado" -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Fecha: ${pedido.fecha}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        "Total: $${String.format("%.0f", pedido.precioTotal)}",
                                        style = MaterialTheme.typography.titleSmall,
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
    }
}

data class PedidoFicticio(
    val codigo: String,
    val fecha: String,
    val precioTotal: Double,
    val estado: String
)

@Preview(showBackground = true)
@Composable
fun PedidosScreenPreview() {
    MaterialTheme {
        PedidosScreen()
    }
}