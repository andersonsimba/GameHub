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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
fun PantallaBusqueda(
    // Inyección del ViewModel para acceder a los datos de la API
    viewModel: VideojuegoViewModel,
    // Evento de navegación hacia la pantalla de detalle
    onVideojuegoClick: (VideojuegoApi) -> Unit = {}
) {
    // ESTADO LOCAL DE BÚSQUEDA: Almacena el texto ingresado por el usuario en tiempo real
    var textoBusqueda by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // CAMPO DE TEXTO DE BÚSQUEDA (SEARCH BAR)
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { nuevoTexto -> textoBusqueda = nuevoTexto },
            label = { Text("Buscar por nombre o género...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Ícono de búsqueda"
                )
            },
            trailingIcon = {
                if (textoBusqueda.isNotEmpty()) {
                    IconButton(onClick = { textoBusqueda = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar texto"
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // CONTROL DE ESTADOS DE LA UI
        when (val estado = viewModel.estadoUi) {
            is EstadoUiVideojuegos.Cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is EstadoUiVideojuegos.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (estado.esErrorConexion) Icons.Default.WifiOff else Icons.Default.ErrorOutline,
                        contentDescription = if (estado.esErrorConexion) "Sin conexión" else "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (estado.esErrorConexion) "¡Sin conexión a internet!" else "Error al cargar la búsqueda",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = estado.mensaje,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.cargarVideojuegos() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reintentar",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = "Reintentar")
                    }
                }
            }

            is EstadoUiVideojuegos.Exito -> {
                // FILTRADO DINÁMICO EN TIEMPO REAL:
                // Se filtran los juegos comparando el título o el género con el texto escrito
                val juegosFiltrados = estado.juegos.filter { juego ->
                    juego.nombre.contains(textoBusqueda, ignoreCase = true) ||
                            (juego.genero?.contains(textoBusqueda, ignoreCase = true) == true)
                }

                if (juegosFiltrados.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron videojuegos que coincidan.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(juegosFiltrados) { videojuego ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onVideojuegoClick(videojuego) }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    AsyncImage(
                                        model = videojuego.imagen,
                                        contentDescription = "Imagen de ${videojuego.nombre}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                    )

                                    Text(
                                        text = videojuego.nombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    Row(modifier = Modifier.padding(top = 4.dp)) {
                                        Text(
                                            text = videojuego.genero ?: "Sin género",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = " • ${videojuego.developer ?: "Desarrollador no disponible"}",
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
    }
}