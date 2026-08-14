package com.example.gamehub.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamehub.data.local.VideojuegoEntity
import com.example.gamehub.data.network.VideojuegoApi
import com.example.gamehub.data.repository.VideojuegoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// DEFINICIÓN DE ESTADOS DE LA UI:
// Representa todos los posibles estados en los que puede estar la pantalla de catálogo/búsqueda.
sealed interface EstadoUiVideojuegos {
    object Cargando : EstadoUiVideojuegos
    data class Exito(val juegos: List<VideojuegoApi>) : EstadoUiVideojuegos
    data class Error(val mensaje: String) : EstadoUiVideojuegos
}

class VideojuegoViewModel(
    private val repository: VideojuegoRepository
) : ViewModel() {

    // ESTADO DE LA UI (Consumido por Compose para renderizar Pantalla Carga / Datos / Error)
    var estadoUi: EstadoUiVideojuegos by mutableStateOf(EstadoUiVideojuegos.Cargando)
        private set

    // Estado y función para gestionar el cambio de modo oscuro en la interfaz de ajustes de la aplicación.
    private val _esModoOscuro = mutableStateOf(false)
    val esModoOscuro: Boolean by _esModoOscuro

    fun cambiarModoOscuro(valor: Boolean) {
        _esModoOscuro.value = valor
    }

    // FLUJO DE FAVORITOS (ROOM):
    // Convierte el Flow de Room a StateFlow para que la UI de Compose reaccione automáticamente
    // en tiempo real cuando un juego es agregado o eliminado de la BD local.
    val listaFavoritos: StateFlow<List<VideojuegoEntity>> = repository.favoritos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Al instanciar el ViewModel, cargamos inmediatamente el catálogo de la API
        cargarVideojuegos()
    }

    // 1. PETICIÓN A LA API (RETROFIT + CORRUTINAS)
    fun cargarVideojuegos() {
        viewModelScope.launch {
            estadoUi = EstadoUiVideojuegos.Cargando
            try {
                // Obtención de datos remotos mediante el Repositorio
                val respuestaApi = repository.obtenerVideojuegos()
                estadoUi = EstadoUiVideojuegos.Exito(respuestaApi)
            } catch (e: Exception) {
                // Captura de errores de red o deserialización
                estadoUi = EstadoUiVideojuegos.Error(
                    e.localizedMessage ?: "Ocurrió un error inesperado al conectar con el servidor"
                )
            }
        }
    }

    // 2. GESTIÓN DE FAVORITOS LOCALES (ROOM)
    fun toggleFavorito(juegoApi: VideojuegoApi, esFavoritoActual: Boolean) {
        viewModelScope.launch {
            // Conversión del DTO de la API a Entidad de Room
            val entidad = VideojuegoEntity(
                id = juegoApi.id,
                nombre = juegoApi.nombre,
                descripcion = juegoApi.descripcion,
                genero = juegoApi.genero,
                imagen = juegoApi.imagen
            )

            if (esFavoritoActual) {
                repository.eliminarFavorito(entidad)
            } else {
                repository.guardarFavorito(entidad)
            }
        }
    }

    // FACTORY PARA CREAR INSTANCIAS DEL VIEWMODEL PASANDO EL REPOSITORIO COMO PARÁMETRO
    class Factory(private val repository: VideojuegoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VideojuegoViewModel::class.java)) {
                return VideojuegoViewModel(repository) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}