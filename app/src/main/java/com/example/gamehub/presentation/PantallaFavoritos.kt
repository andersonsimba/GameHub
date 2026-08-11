package com.example.gamehub.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PantallaFavoritos(
    // Inyección del ViewModel para acceder al flujo de datos locales (Room)
    viewModel: VideojuegoViewModel
) {
    // OBSERVACIÓN EN TIEMPO REAL: Convierte el StateFlow de Room en un estado legible por Jetpack Compose
    val favoritos by viewModel.listaFavoritos.collectAsState()

    if (favoritos.isEmpty()) {
        // Mensaje cuando la base de datos local está vacía
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No tienes videojuegos añadidos a favoritos.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Renderiza la lista de elementos persistidos en la BD de Room (VideojuegoEntity)
            items(favoritos) { juego ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Imagen persistida
                        AsyncImage(
                            model = juego.imagen,
                            contentDescription = "Imagen de ${juego.nombre}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = juego.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = juego.genero ?: "Sin género",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // Botón para eliminar directamente de la BD local (Room)
                            IconButton(
                                onClick = {
                                    // Creamos un objeto API dummy o llamamos a eliminar en Room mediante ViewModel
                                    val juegoApi = com.example.gamehub.data.network.VideojuegoApi(
                                        id = juego.id,
                                        nombre = juego.nombre,
                                        descripcion = juego.descripcion,
                                        genero = juego.genero,
                                        developer = null,
                                        fechaLanzamiento = null,
                                        platform = null,
                                        imagen = juego.imagen
                                    )
                                    viewModel.toggleFavorito(juegoApi, esFavoritoActual = true)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar de favoritos",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}