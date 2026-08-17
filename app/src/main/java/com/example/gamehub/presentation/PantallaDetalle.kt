package com.example.gamehub.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gamehub.data.network.VideojuegoApi

/**
 * PANTALLA DE DETALLE Y RESEÑA
 * Muestra la información completa de un videojuego seleccionado,
 * permite gestionarlo en la lista de favoritos y guardar o eliminar
 * reseñas personalizadas en la base de datos local (Room).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    id: Int,
    nombre: String,
    descripcion: String,
    genero: String,
    developer: String,
    fechaLanzamiento: String,
    platform: String,
    imagen: String,
    viewModel: VideojuegoViewModel
) {
    // RECONSTRUCCIÓN DEL OBJETO DTO: Se agrupan los parámetros para interactuar con la lógica de negocio
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

    // ESTADO OBSERVABLE: Obtiene la lista actualizada de favoritos en tiempo real desde el ViewModel
    val favoritos by viewModel.listaFavoritos.collectAsState()
    val esFavorito = favoritos.any { it.id == id }

    // ESTADOS LOCALES REACTIVOS: Se sincronizan dinámicamente según el ID del videojuego activo
    var calificacion by remember(id) { mutableIntStateOf(5) }
    var comentario by remember(id) { mutableStateOf("") }
    var mensajeConfirmacion by remember { mutableStateOf("") }
    var tieneResenaGuardada by remember(id) { mutableStateOf(false) }

    // EFECTO DE CARGA ASÍNCRONA: Al cargar la pantalla, consulta si ya existe una reseña local en Room
    LaunchedEffect(id) {
        val resenaExistente = viewModel.obtenerResenaLocal(id)
        if (resenaExistente != null) {
            calificacion = resenaExistente.calificacion
            comentario = resenaExistente.comentario
            tieneResenaGuardada = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nombre.ifEmpty { "Detalle del juego" }) },
                actions = {
                    // BOTÓN DE FAVORITOS: Llama al ViewModel para agregar o quitar de la BD local
                    IconButton(onClick = { viewModel.toggleFavorito(juegoApi, esFavorito) }) {
                        Icon(
                            imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (esFavorito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Permite el desplazamiento vertical si el texto es extenso
                .padding(16.dp)
        ) {
            // CARGA DE IMAGEN REMOTA: Uso de la librería Coil para renderizar imágenes mediante URL
            if (imagen.isNotEmpty()) {
                AsyncImage(
                    model = imagen,
                    contentDescription = nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // METADATOS DEL VIDEOJUEGO
            Text(text = nombre, style = MaterialTheme.typography.headlineMedium)
            if (genero.isNotEmpty()) Text(text = "Género: $genero", style = MaterialTheme.typography.bodyMedium)
            if (developer.isNotEmpty()) Text(text = "Desarrollador: $developer", style = MaterialTheme.typography.bodyMedium)
            if (platform.isNotEmpty()) Text(text = "Plataforma: $platform", style = MaterialTheme.typography.bodyMedium)
            if (fechaLanzamiento.isNotEmpty()) Text(text = "Fecha: $fechaLanzamiento", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))

            // SECCIÓN DE DESCRIPCIÓN DETALLADA
            Text(
                text = "Descripción:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = descripcion.ifEmpty { "Sin descripción disponible." },
                style = MaterialTheme.typography.bodyLarge
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // SECCIÓN DE INTERACCIÓN: SISTEMA DE RESEÑAS
            Text(text = "Tu Reseña", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            // SECCIÓN DE CALIFICACIÓN POR ESTRELLAS INTERACTIVAS
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = "Calificación: ", style = MaterialTheme.typography.bodyMedium)

                // Renderiza 5 estrellas interactivas con estados activos (amarillo) e inactivos (gris)
                for (posicion in 1..5) {
                    IconButton(
                        onClick = { calificacion = posicion } // Actualiza el estado reactivo
                    ) {
                        Icon(
                            imageVector = if (posicion <= calificacion) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Estrella $posicion",
                            tint = if (posicion <= calificacion) Color(0xFFFFC107) else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // CAMPO DE TEXTO PARA OPINIÓN DEL USUARIO
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Escribe tu opinión...") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(12.dp))

            // BOTONES DE ACCIÓN (GUARDAR / ELIMINAR)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ACCIÓN GUARDAR: Persiste la reseña en Room mediante Corrutinas en el ViewModel
                Button(
                    onClick = {
                        viewModel.guardarResena(
                            idVideojuego = id,
                            nombreVideojuego = nombre,
                            calificacion = calificacion,
                            comentario = comentario
                        )
                        tieneResenaGuardada = true
                        mensajeConfirmacion = "¡Reseña guardada!"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar Reseña")
                }

                // ACCIÓN ELIMINAR: Elimina el registro persistido en la BD local
                if (tieneResenaGuardada) {
                    OutlinedButton(
                        onClick = {
                            viewModel.eliminarResena(id)
                            comentario = ""
                            calificacion = 5
                            tieneResenaGuardada = false
                            mensajeConfirmacion = "Reseña eliminada"
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Eliminar")
                    }
                }
            }

            // FEEDBACK VISUAL PARA EL USUARIO
            if (mensajeConfirmacion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = mensajeConfirmacion,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}