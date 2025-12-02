package com.example.desarrollomovil.vistas.crud

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.example.desarrollomovil.data.*
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.desarrollomovil.viewmodels.JuegoViewModel
import com.example.desarrollomovil.vistas.vibrar
import java.io.File
import java.util.concurrent.Executors

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarJuegoScreen(
    juegoId: Int,
    onBack: () -> Unit = {},
    onEditarSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val juegoViewModel = viewModel<JuegoViewModel>()
    var isLoading by remember { mutableStateOf(true) }

    //Carga de datos de Juego
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val juego = juegoViewModel.obtenerJuegoPorId(juegoId)
        } catch (e:Exception){
            Toast.makeText(context, "Error al cargar el juego", Toast.LENGTH_SHORT).show()
        }finally {
            isLoading = false
        }
    }
    //State para traer informacion del juego
    var titulo = juegoViewModel.state.titulo
    var publicador = juegoViewModel.state.publicador
    var precio = juegoViewModel.state.precio
    var stock = juegoViewModel.state.stock
    var descripcion = juegoViewModel.state.descripcion
    var plataforma = juegoViewModel.state.plataforma
    var genero = juegoViewModel.state.genero
    var imagenurl = juegoViewModel.state.imagenurl

    //Variables para Seleccion de Imagen
    var mostrarDialogoSeleccion by remember { mutableStateOf(false) }
    var camaraAbierta by remember { mutableStateOf(false) }
    val ejecutarCamara = remember { Executors.newSingleThreadExecutor() }
    val capturaFoto = remember { ImageCapture.Builder().build() }
    val proveedorCamara = remember { ProcessCameraProvider.getInstance(context) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    val mensaje by juegoViewModel.mensaje.collectAsState()

    //Permisos y funciones camara
    val lifecycle = LocalLifecycleOwner.current
    var tenemosPermisoCamara by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val lanzarPermisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()) { granted ->
        tenemosPermisoCamara = granted
        if (granted) {
            camaraAbierta = true
        }
    }
    //Abrir Galeria
    val lanzadorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val archivo = File.createTempFile(
                    "galeria_${System.currentTimeMillis()}",
                    ".jpg",
                    context.cacheDir
                )
                inputStream?.use { input ->
                    archivo.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                imagenUri = Uri.fromFile(archivo)

            } catch (e: Exception) {
                vibrar(context)
                Toast.makeText(context, "Error al cargar imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //Mensaje cuando se actualize el juego sin error
    LaunchedEffect(mensaje) {
       if (mensaje != null && mensaje!!.contains("exito")) {
           isLoading = false
           Toast.makeText(context, "Juego Actualizado", Toast.LENGTH_LONG).show()
           onEditarSuccess()
        }
    }
    //Mostrar alert con seleccion para imagen
    if (mostrarDialogoSeleccion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoSeleccion = false },
            title = { Text("Seleccionar Imagen") },
            text = { Text("¿Cómo quieres agregar la imagen del juego?") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoSeleccion = false
                        if (tenemosPermisoCamara) {
                            camaraAbierta = true
                        } else {
                            lanzarPermisoCamara.launch(Manifest.permission.CAMERA)
                        }
                    }
                ) {
                    Text("Tomar Foto")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        mostrarDialogoSeleccion = false
                        lanzadorGaleria.launch("image/*")
                    }
                ) {
                    Text("Desde Galería")
                }
            }
        )
    }
    //Interfaz de Camara
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
                                    Toast.makeText(context, "Error al tomar foto", Toast.LENGTH_SHORT).show()
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
                    onClick = {
                        camaraAbierta = false
                        mostrarDialogoSeleccion = true
                    },
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
                    title = { Text("Editar Juego") },
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
                    }
                }
            } else {
                CenteredColumnWithSpacing(16.dp, paddingValues) {

                    AsyncImage(
                        model = imagenurl,
                        contentDescription = "Portada de ${juegoViewModel.cTitulo(titulo)}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { juegoViewModel.cTitulo(it)},
                        label = { Text("Titulo *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = publicador,
                        onValueChange = { juegoViewModel.cPublicador(it) },
                        label = { Text("Publicador *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { juegoViewModel.cPrecio(it) },
                        label = { Text("Precio *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { juegoViewModel.cStock(it) },
                        label = { Text("Stock *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { juegoViewModel.cDesc(it) },
                        label = { Text("Descripcion") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 3
                    )
                    OutlinedTextField(
                        value = plataforma,
                        onValueChange = { juegoViewModel.cPlataforma(it) },
                        label = { Text("Plataforma *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = genero,
                        onValueChange = { juegoViewModel.cGenero(it) },
                        label = { Text("Genero *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            mostrarDialogoSeleccion = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cambiar Imagen")
                    }

                    imagenUri?.let { uri ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Nueva Imagen:", color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            AsyncImage(
                                model = uri,
                                contentDescription = "Imagen",
                                modifier = Modifier
                                    .size(150.dp)
                                    .padding(8.dp)
                            )
                        }
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
                            isLoading = true
                            val juegoEditado = Juego(
                                id = juegoId,
                                titulo = juegoViewModel.state.titulo,
                                publicador = juegoViewModel.state.publicador,
                                precio = juegoViewModel.state.precio.toInt(),
                                stock = juegoViewModel.state.stock.toInt(),
                                descripcion = juegoViewModel.state.descripcion,
                                plataforma = juegoViewModel.state.plataforma,
                                genero = juegoViewModel.state.genero,
                                imagenurl = juegoViewModel.state.imagenurl
                            )
                            try {
                                juegoViewModel.actualizarJuego(juegoEditado,imagenUri?.toFile())
                                juegoViewModel.limpiarMensaje()
                            }catch (e:Exception){
                                isLoading = false
                                Toast.makeText(context, "Error al Editar el Juego", Toast.LENGTH_LONG).show()
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = titulo.isNotBlank() && publicador.isNotBlank() && precio.isNotBlank() && stock.isNotBlank() && plataforma.isNotBlank() && genero.isNotBlank()
                    ) {
                        Text("Actualizar Juego")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun EditarJuegoScreenPreview() {
    EditarJuegoScreen(juegoId = 0)
}