package com.example.gamehub.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream

@Composable
fun PantallaPerfil(
    viewModel: VideojuegoViewModel? = null
) {
    val context = LocalContext.current

    // Observa el nombre guardado en DataStore
    val nombreUsuario by viewModel?.nombreUsuario?.collectAsState() ?: remember { mutableStateOf("") }

    // Observa los favoritos desde Room
    val favoritos by viewModel?.listaFavoritos?.collectAsState() ?: remember { mutableStateOf(emptyList()) }

    // Observa el URI persistente de la foto desde DataStore
    val fotoPerfilUri by viewModel?.fotoPerfilUri?.collectAsState() ?: remember { mutableStateOf("") }

    // Helper para guardar una imagen Bitmap en el almacenamiento interno privado del dispositivo
    fun guardarImagenEnAlmacenamientoInterno(bitmap: Bitmap): Uri? {
        return try {
            val archivo = File(context.filesDir, "foto_perfil.jpg")
            val stream = FileOutputStream(archivo)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            stream.flush()
            stream.close()
            Uri.fromFile(archivo)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Helper para guardar un Uri proveniente de la galería
    fun guardarUriEnAlmacenamientoInterno(uri: Uri): Uri? {
        return try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
            guardarImagenEnAlmacenamientoInterno(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Launcher para capturar foto de la cámara
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val uriGuardado = guardarImagenEnAlmacenamientoInterno(bitmap)
            uriGuardado?.let { viewModel?.guardarFotoPerfilUri(it.toString()) }
        }
    }

    // Launcher para solicitar permiso de cámara
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido) {
            camaraLauncher.launch(null)
        }
    }

    // Launcher para seleccionar imagen de la Galería
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val uriGuardado = guardarUriEnAlmacenamientoInterno(it)
            uriGuardado?.let { uriPersistente ->
                viewModel?.guardarFotoPerfilUri(uriPersistente.toString())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Perfil del Jugador",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contenedor de la Foto de Perfil con soporte para imágenes persistentes
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (fotoPerfilUri.isNotEmpty()) {
                AsyncImage(
                    model = fotoPerfilUri,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "Sin foto",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (nombreUsuario.isNotEmpty()) nombreUsuario else "Usuario GameHub",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "🎮 Juegos favoritos: ${favoritos.size}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    val permiso = Manifest.permission.CAMERA
                    if (ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED) {
                        camaraLauncher.launch(null)
                    } else {
                        permisoCamaraLauncher.launch(permiso)
                    }
                }
            ) {
                Text(text = "Cámara")
            }

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedButton(
                onClick = {
                    galeriaLauncher.launch("image/*")
                }
            ) {
                Text(text = "Galería")
            }
        }
    }
}