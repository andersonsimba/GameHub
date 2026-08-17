package com.example.gamehub.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gamehub.data.network.VideojuegoApi

@Composable
fun PantallaDetalle(
    id: Int = 0,
    nombre: String,
    descripcion: String,
    genero: String,
    developer: String,
    fechaLanzamiento: String,
    platform: String,
    imagen: String,
    viewModel: VideojuegoViewModel? = null
) {
    val favoritos = viewModel?.listaFavoritos?.collectAsState(initial = emptyList())?.value ?: emptyList()
    val esFavorito = favoritos.any { it.id == id }

    // Calcula la calificación determinista con base en el ID
    val calificacion = if (id != 0) String.format("%.1f", 3.5 + (id % 16) * 0.1) else "4.5"

    Scaffold(
        floatingActionButton = {
            if (viewModel != null && id != 0) {
                FloatingActionButton(
                    onClick = {
                        val juegoApi = VideojuegoApi(
                            id = id,
                            nombre = nombre,
                            descripcion = descripcion,
                            genero = genero,
                            developer = developer,
                            fechaLanzamiento = fechaLanzamiento,
                            platform = platform,
                            imagen = imagen
                        )
                        viewModel.toggleFavorito(juegoApi, esFavoritoActual = esFavorito)
                    }
                ) {
                    Icon(
                        imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Guardar en Favoritos",
                        tint = if (esFavorito) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = imagen,
                contentDescription = "Imagen de $nombre",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = nombre,
                style = MaterialTheme.typography.headlineMedium
            )

            // Indicador de Calificación
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Calificación",
                    tint = Color(0xFFFFC107) // Color Dorado Star
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$calificacion / 5.0",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Género: ${genero.ifEmpty { "No especificado" }}",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Plataforma: ${platform.ifEmpty { "No especificada" }}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Desarrollador: ${developer.ifEmpty { "No especificado" }}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Lanzamiento: ${fechaLanzamiento.ifEmpty { "No especificado" }}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = descripcion.ifEmpty { "Sin descripción disponible." },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}