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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
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
    // Escucha el nombre almacenado en DataStore mediante StateFlow
    val nombreUsuario by viewModel.nombreUsuario.collectAsState()

    // Cantidad de videojuegos visibles inicialmente y después de cada ampliación
    var cantidadVisible by remember { mutableIntStateOf(10) }

    // Reinicia la cantidad visible cuando se obtiene una nueva lista desde la API
    LaunchedEffect(viewModel.estadoUi) {
        if (viewModel.estadoUi is EstadoUiVideojuegos.Exito) {
            cantidadVisible = 10
        }
    }

    when (val estado = viewModel.estadoUi) {

        // ESTADO DE CARGA
        is EstadoUiVideojuegos.Cargando -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // ESTADO DE ERROR PERSONALIZADO Y AMIGABLE
        is EstadoUiVideojuegos.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (estado.esErrorConexion) Icons.Default.WifiOff else Icons.Default.ErrorOutline,
                    contentDescription = if (estado.esErrorConexion) "Sin conexión" else "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (estado.esErrorConexion) "¡Sin conexión a internet!" else "No se pudo cargar el catálogo",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = estado.mensaje,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.cargarVideojuegos() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reintentar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Reintentar conexión")
                }
            }
        }

        // ESTADO DE ÉXITO
        is EstadoUiVideojuegos.Exito -> {

            val juegosVisibles = estado.juegos.take(cantidadVisible)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Muestra un saludo personalizado si el usuario ya registró su nombre
                if (nombreUsuario.isNotEmpty()) {
                    item {
                        Text(
                            text = "¡Hola Bienvenido, $nombreUsuario! 👋",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                // Muestra únicamente la cantidad de videojuegos actualmente habilitada
                items(juegosVisibles) { videojuego ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onVideojuegoClick(videojuego)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            // Carga asíncrona de imágenes mediante Coil
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

                // Botón para cargar 10 videojuegos adicionales
                if (cantidadVisible < estado.juegos.size) {
                    item {
                        Button(
                            onClick = {
                                cantidadVisible += 10
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text("Mostrar más")
                        }
                    }
                }
            }
        }
    }
}