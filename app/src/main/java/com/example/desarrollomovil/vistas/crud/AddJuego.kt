package com.example.desarrollomovil.vistas.crud

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.desarrollomovil.data.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.desarrollomovil.viewmodels.JuegoViewModel
import com.example.desarrollomovil.viewmodels.JuegoViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarJuegoScreen(onBack: () -> Unit = {}, onAgregarSuccess: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = LibreriaDatabase.getDatabase(context)
    val repository = JuegoRepository(database.juegoDao())
    val juegoViewModel: JuegoViewModel = viewModel(
        factory = JuegoViewModelFactory(repository)
    )


    var titulo by remember { mutableStateOf("") }
    var publicador by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var plataforma by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }

    val lifecycle = LocalLifecycleOwner.current
    var tenemosPermisoCamara by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val lanzarPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()) { granted ->
        tenemosPermisoCamara = granted
    }
    var camaraAbierta by remember { mutableStateOf(false) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val ejecutarCamara = remember { Executors.newSingleThreadExecutor() }
    val capturaFoto = remember { ImageCapture.Builder().build() }
    val proveedorCamara = remember { ProcessCameraProvider.getInstance(context) }

    val mensaje by juegoViewModel.mensaje.collectAsState()

    LaunchedEffect(mensaje) {
        if (mensaje != null && mensaje!!.contains("exito")) {
            Toast.makeText(context, "Juego agregado", Toast.LENGTH_LONG).show()
            titulo = ""; publicador = ""; precio = ""; stock = ""; descripcion = ""; plataforma = ""; genero = ""; imagenUri = null
            onAgregarSuccess()
        }
    }

    if (camaraAbierta && tenemosPermisoCamara) {
        Box(Modifier.fillMaxSize()) {
            AndroidView(factory = { ctx ->
                PreviewView(ctx).apply {
                    val cameraProvider = proveedorCamara.get()
                    val vistaPrevia = CameraPreview.Builder().build().also {
                        it.setSurfaceProvider(this.surfaceProvider)
                    }
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycle,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            vistaPrevia,
                            capturaFoto
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }, modifier = Modifier.fillMaxSize())

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        val archivoFoto = File(context.cacheDir, "foto_${System.currentTimeMillis()}.jpg")
                        val salidaFoto = ImageCapture.OutputFileOptions.Builder(archivoFoto).build()
                        capturaFoto.takePicture(
                            salidaFoto,
                            ejecutarCamara,
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(resultadoSalidaImagen: ImageCapture.OutputFileResults) {
                                    imagenUri = Uri.fromFile(archivoFoto)
                                    camaraAbierta = false
                                }

                                override fun onError(exception: ImageCaptureException) {
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tomar Foto")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { camaraAbierta = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Agregar Juego")},
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Volver")
                        }
                    }
                )
            }
        ) { paddingValues ->
            CenteredColumnWithSpacing (16.dp, paddingValues) {
                OutlinedTextField(
                    value = titulo, onValueChange = { titulo = it },
                    label = { Text("Titulo *") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = publicador, onValueChange = { publicador = it },
                    label = { Text("Publicador *") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = precio, onValueChange = { precio = it },
                    label = { Text("Precio *") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = stock, onValueChange = { stock = it },
                    label = { Text("Stock *") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = descripcion, onValueChange = { descripcion = it },
                    label = { Text("Descripcion") },
                    modifier = Modifier.fillMaxWidth().height(100.dp), maxLines = 3
                )
                OutlinedTextField(
                    value = plataforma, onValueChange = { plataforma = it },
                    label = { Text("Plataforma *") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = genero, onValueChange = { genero = it },
                    label = { Text("Genero *") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )

                Button(
                    onClick = {
                        if (tenemosPermisoCamara) {
                            camaraAbierta = true
                        } else {
                            lanzarPermiso.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (imagenUri != null && imagenUri?.toString()?.isNotBlank() == true){
                        Text("Cambiar Foto")
                    } else{
                        Text("Abrir Camara")
                    }
                }

                imagenUri?.let { uri ->
                    Text("Foto Lista", color = MaterialTheme.colorScheme.primary)
                }

                mensaje?.let {
                    Text(
                        text = it,
                        color = if (it.contains("Exito")) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = {
                        juegoViewModel.limpiarMensaje()
                        val precioNum = precio.toDoubleOrNull() ?: 0.0
                        val stockNum = stock.toIntOrNull() ?: 0

                        juegoViewModel.agregarJuego(
                            titulo = titulo,
                            publicador = publicador,
                            precio = precioNum,
                            stock = stockNum,
                            descripcion = descripcion,
                            plataforma = plataforma,
                            genero = genero,
                            imagenUri = imagenUri?.toString() ?: ""
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = titulo.isNotBlank() && publicador.isNotBlank() && precio.isNotBlank() && stock.isNotBlank() && plataforma.isNotBlank() && genero.isNotBlank() && imagenUri.toString().isNotBlank() && imagenUri != null
                ) {
                    Text("Agregar Juego")
                }
            }
        }
    }
}

@Preview
@Composable
fun AgregarJuegoScreenPreview() {
    AgregarJuegoScreen()
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
            .padding (16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            content()
        }
    }
}