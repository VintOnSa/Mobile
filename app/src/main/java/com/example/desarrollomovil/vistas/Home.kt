package com.example.desarrollomovil.vistas

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.desarrollomovil.ui.theme.DesarrolloMovilTheme
import com.example.desarrollomovil.viewmodels.JuegoViewModel

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.example.desarrollomovil.R
import java.text.DecimalFormat


@Composable
fun Home(
    userType: String,
    onProfileAction: () -> Unit,
    onLogout: () -> Unit,
    onClickJuego: (Int) -> Unit = {},
    toCarrito: () -> Unit,
) {
    val context = LocalContext.current

    val juegoViewModel = viewModel<JuegoViewModel>()
    val state = juegoViewModel.state

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

    Column(modifier = Modifier.fillMaxSize()) {
        TopBarEditada(
            userType = userType,
            onProfileAction = onProfileAction,
            onLogout = onLogout,
            toCarrito = toCarrito
        )
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                text = "Ultimas Adiciones",
                style = MaterialTheme.typography.headlineMedium
            )}

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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.juego) { juego ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        onClick = {onClickJuego(juego.id)}
                    ) {
                        Column {
                            AsyncImage(
                                model = juego.imagenurl,
                                contentDescription = "Portada",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .padding(top = 20.dp)
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    juego.titulo,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    "$${formato.format(juego.precio)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TopBarEditada(
    userType: String,
    onProfileAction: () -> Unit,
    onLogout: () -> Unit,
    toCarrito: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF006064))
            .padding(20.dp, top = 35.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            onProfileAction()
        }, modifier = Modifier
            .size(40.dp)
            .padding(end = 16.dp)) {
            Icon(Icons.Default.AccountCircle, "Usuario", tint = Color.White)

        }
        Image(
            painter = painterResource(id = R.drawable.ic_launcher),
            contentDescription = "Icono",
            modifier = Modifier.size(36.dp)
        )

        IconButton(
            onClick = toCarrito,
            modifier = Modifier
            .size(40.dp)
            .padding(end = 16.dp)) {
            Icon(Icons.Default.ShoppingCart, "Carrito", tint = Color.White)

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun HomePreview() {
    DesarrolloMovilTheme {
        Home(
            userType = "usuario",
            onProfileAction = {},
            onLogout = {},
            onClickJuego = {},
            toCarrito = {}
        )
    }
}

@Composable
fun CenteredColumnWithSpacing(
    spacing: Dp = 16.dp,
    paddingValues: PaddingValues,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically)
    ) {
        content()
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.VIBRATE)
fun vibrar(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                1500,
                100
            )
        )
    } else {
        vibrator.vibrate(1500)
    }
}