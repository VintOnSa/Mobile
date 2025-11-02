package com.example.desarrollomovil.vistas

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
import com.example.desarrollomovil.data.LibreriaDatabase
import com.example.desarrollomovil.data.JuegoRepository
import com.example.desarrollomovil.ui.theme.DesarrolloMovilTheme
import com.example.desarrollomovil.viewmodels.JuegoViewModel
import com.example.desarrollomovil.viewmodels.JuegoViewModelFactory

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.example.desarrollomovil.R


@Composable
fun Home(
    userType: String,
    onProfileAction: () -> Unit,
    onLogout: () -> Unit,
    onClickJuego: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val database = LibreriaDatabase.getDatabase(context)
    val repository = JuegoRepository(database.juegoDao())
    val juegoViewModel: JuegoViewModel = viewModel(
        factory = JuegoViewModelFactory(repository)
    )

    val juegos = juegoViewModel.todosLosJuegos.collectAsState(initial = emptyList()).value.sortedBy { it.id }
    val primerJuego = juegos.firstOrNull()

    Column(modifier = Modifier.fillMaxSize()) {
        TopBarSimple(
            userType = userType,
            onProfileAction = onProfileAction,
            onLogout = onLogout
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



        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(juegos) { juego ->
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
                            model = juego.imagenUri,
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
                                "$${juego.precio.toInt()}",
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


@Composable
fun TopBarSimple(
    userType: String,
    onProfileAction: () -> Unit,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF006064))
            .padding(20.dp, top = 30.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher),
            contentDescription = "Icono",
            modifier = Modifier.size(36.dp)
        )

        IconButton(onClick = {
            println("DEBUG: Icono clickeado, userType: $userType")
            onProfileAction()
        }, modifier = Modifier
            .size(40.dp)
            .padding(end = 16.dp)) {
            Icon(Icons.Default.AccountCircle, "Usuario", tint = Color.White)

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    DesarrolloMovilTheme {
        Home(
            userType = "usuario",
            onProfileAction = {},
            onLogout = {},
            onClickJuego = {}
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
