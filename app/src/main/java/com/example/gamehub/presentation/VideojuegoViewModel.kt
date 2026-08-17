package com.example.gamehub.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamehub.data.local.PreferenciasUsuario
import com.example.gamehub.data.local.ResenaEntity
import com.example.gamehub.data.local.VideojuegoEntity
import com.example.gamehub.data.network.VideojuegoApi
import com.example.gamehub.data.repository.VideojuegoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Representa los posibles estados de la interfaz al cargar videojuegos.
sealed interface EstadoUiVideojuegos {
    object Cargando : EstadoUiVideojuegos
    data class Exito(val juegos: List<VideojuegoApi>) : EstadoUiVideojuegos
    data class Error(val mensaje: String) : EstadoUiVideojuegos
}

class VideojuegoViewModel(
    private val repository: VideojuegoRepository,
    private val preferenciasUsuario: PreferenciasUsuario
) : ViewModel() {

    // Estado de la UI utilizado por catálogo y búsqueda.
    var estadoUi: EstadoUiVideojuegos by mutableStateOf(EstadoUiVideojuegos.Cargando)
        private set

    // Lee el modo oscuro directamente desde DataStore y conserva su valor.
    val esModoOscuro: StateFlow<Boolean> = preferenciasUsuario.modoOscuroFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun cambiarModoOscuro(valor: Boolean) {
        viewModelScope.launch {
            preferenciasUsuario.guardarModoOscuro(valor)
        }
    }

    // Lee el nombre del usuario desde DataStore mediante un flujo observable.
    val nombreUsuario: StateFlow<String> = preferenciasUsuario.nombreUsuarioFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    // Guarda el nombre introducido en el registro dentro de DataStore.
    fun guardarNombreUsuario(nombre: String) {
        viewModelScope.launch {
            preferenciasUsuario.guardarNombreUsuario(nombre)
        }
    }

    // Lee el URI de la foto de perfil persistente almacenado en DataStore.
    val fotoPerfilUri: StateFlow<String> = preferenciasUsuario.fotoPerfilUriFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    // Guarda la ruta/URI de la foto de perfil en DataStore.
    fun guardarFotoPerfilUri(uriString: String) {
        viewModelScope.launch {
            preferenciasUsuario.guardarFotoPerfilUri(uriString)
        }
    }

    // Flujo de favoritos almacenados en Room.
    val listaFavoritos: StateFlow<List<VideojuegoEntity>> = repository.favoritos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        cargarVideojuegos()
    }

    // Solicita los videojuegos mediante Retrofit a través del Repository.
    fun cargarVideojuegos() {
        viewModelScope.launch {
            estadoUi = EstadoUiVideojuegos.Cargando

            try {
                val respuestaApi = repository.obtenerVideojuegos()
                estadoUi = EstadoUiVideojuegos.Exito(respuestaApi)
            } catch (e: Exception) {
                estadoUi = EstadoUiVideojuegos.Error(
                    e.localizedMessage
                        ?: "Ocurrió un error inesperado al conectar con el servidor"
                )
            }
        }
    }

    // Agrega o elimina un videojuego de los favoritos almacenados en Room.
    fun toggleFavorito(
        juegoApi: VideojuegoApi,
        esFavoritoActual: Boolean
    ) {
        viewModelScope.launch {
            val entidad = VideojuegoEntity(
                id = juegoApi.id,
                nombre = juegoApi.nombre,
                descripcion = juegoApi.descripcion ?: "",
                genero = juegoApi.genero ?: "Sin género",
                imagen = juegoApi.imagen ?: ""
            )

            if (esFavoritoActual) {
                repository.eliminarFavorito(entidad)
            } else {
                repository.guardarFavorito(entidad)
            }
        }
    }

    // Guarda o actualiza una reseña en Room a través del Repository.
    fun guardarResena(idVideojuego: Int, nombreVideojuego: String, calificacion: Int, comentario: String) {
        viewModelScope.launch {
            val resena = ResenaEntity(
                idVideojuego = idVideojuego,
                nombreVideojuego = nombreVideojuego,
                calificacion = calificacion,
                comentario = comentario
            )
            repository.guardarResena(resena)
        }
    }

    // Consulta la reseña almacenada localmente para un videojuego por su ID.
    suspend fun obtenerResenaLocal(idVideojuego: Int): ResenaEntity? {
        return repository.obtenerResena(idVideojuego)
    }

    // Elimina la reseña almacenada de un videojuego en Room.
    // Elimina la reseña almacenada de un videojuego en Room recibiendo el ID.
    fun eliminarResena(idVideojuego: Int) {
        viewModelScope.launch {
            repository.eliminarResena(idVideojuego)
        }
    }

    // Factory para crear el ViewModel con Repository y DataStore.
    class Factory(
        private val repository: VideojuegoRepository,
        private val preferenciasUsuario: PreferenciasUsuario
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VideojuegoViewModel::class.java)) {
                return VideojuegoViewModel(
                    repository,
                    preferenciasUsuario
                ) as T
            }

            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}