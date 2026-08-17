package com.example.gamehub.presentation

import androidx.compose.foundation.clickable
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
import com.example.gamehub.data.repository.aApi

/**
 * PANTALLA DE FAVORITOS
 * Muestra la lista de videojuegos almacenados localmente en la base de datos (Room).
 * Permite eliminar un registro o navegar a la pantalla de detalle preservando toda la información.
 */
@Composable
fun PantallaFavoritos(
    // Inyección del ViewModel para acceder al flujo de datos locales (Room)
    viewModel: VideojuegoViewModel,
    // Callback para navegar a la PantallaDetalle cuando el usuario pulsa un juego
    onVideojuegoClick: (com.example.gamehub.data.network.VideojuegoApi) -> Unit = {}
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
            items(favoritos) { juegoEntity ->
                // MAPEO A OBJETO DTO: Se utiliza la función de extensión `aApi()` para incluir
                // desarrollador, fecha de lanzamiento y plataforma al navegar hacia la pantalla de detalle.
                val juegoApi = juegoEntity.aApi()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onVideojuegoClick(juegoApi) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Imagen persistida mediante Coil
                        AsyncImage(
                            model = juegoEntity.imagen,
                            contentDescription = "Imagen de ${juegoEntity.nombre}",
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
                                    text = juegoEntity.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = juegoEntity.genero.ifEmpty { "Sin género" },
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // Botón para eliminar directamente de la BD local (Room)
                            IconButton(
                                onClick = {
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