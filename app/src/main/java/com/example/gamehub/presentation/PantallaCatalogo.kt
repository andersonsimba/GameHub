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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gamehub.data.network.VideojuegoApi

@Composable
fun PantallaCatalogo(
    // Se recibe el ViewModel inyectado desde el NavGraph
    viewModel: VideojuegoViewModel,

    // Evento para navegar al detalle al pulsar sobre una tarjeta
    onVideojuegoClick: (VideojuegoApi) -> Unit
) {
    // EVALUACIÓN DEL ESTADO DE LA UI (CARGANDO / ÉXITO / ERROR)
    when (val estado = viewModel.estadoUi) {

        // 1. ESTADO DE CARGA: Muestra un indicador circular centrado en pantalla
        is EstadoUiVideojuegos.Cargando -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // 2. ESTADO DE ERROR: Informa al usuario sobre fallos de conexión y permite reintentar
        is EstadoUiVideojuegos.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ocurrió un error al cargar el catálogo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = estado.mensaje,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Button(
                    onClick = { viewModel.cargarVideojuegos() }
                ) {
                    Text(text = "Reintentar")
                }
            }
        }

        // 3. ESTADO DE ÉXITO: Renderiza la lista optimizada de videojuegos
        is EstadoUiVideojuegos.Exito -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // LazyColumn recicla los componentes gráficos para optimizar memoria en listas largas
                items(estado.juegos) { videojuego ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onVideojuegoClick(videojuego) }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Carga asíncrona de imágenes remotas mediante Coil
                            AsyncImage(
                                model = videojuego.imagen,
                                contentDescription = "Imagen de ${videojuego.nombre}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )

                            Text(
                                text = videojuego.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Row(
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = videojuego.genero ?: "Sin género",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = " • ${videojuego.developer ?: "Desarrollador desconocido"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}