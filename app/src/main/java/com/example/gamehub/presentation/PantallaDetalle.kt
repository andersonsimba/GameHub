package com.example.gamehub.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
    // Inyección opcional del ViewModel para alternar favoritos directamente desde el detalle
    viewModel: VideojuegoViewModel? = null
) {
    // Observa si el juego actual ya existe en la base de datos local Room
    val favoritos = viewModel?.listaFavoritos?.collectAsState(initial = emptyList())?.value ?: emptyList()
    val esFavorito = favoritos.any { it.id == id }

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
                .verticalScroll(rememberScrollState()) // Permite desplazamiento cuando la descripción es muy larga
        ) {
            // IMAGEN PRINCIPAL
            AsyncImage(
                model = imagen,
                contentDescription = "Imagen de $nombre",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TÍTULO DEL VIDEOJUEGO
            Text(
                text = nombre,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // GÉNERO Y PLATAFORMA
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

            // DESCRIPCIÓN DETALLADA
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