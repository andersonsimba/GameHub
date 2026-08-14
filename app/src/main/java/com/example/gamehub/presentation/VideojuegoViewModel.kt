package com.example.gamehub.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamehub.data.local.AppDatabase
import com.example.gamehub.data.local.PreferenciasUsuario
import com.example.gamehub.data.repository.VideojuegoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel principal de la aplicación con acceso al contexto (AndroidViewModel)
class VideojuegoViewModel(application: Application) : AndroidViewModel(application) {

    // Instancia de las preferencias del usuario (DataStore)
    private val preferenciasUsuario = PreferenciasUsuario(application)

    // Inicialización del repositorio unificado (Room + Retrofit)
    private val dao = AppDatabase.getDatabase(application).videojuegoDao()
    private val repositorio = VideojuegoRepository(dao)

    // Estado reactivo (StateFlow) del modo oscuro para la UI
    val esModoOscuro: StateFlow<Boolean> = preferenciasUsuario.modoOscuroFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // Función para cambiar y guardar la preferencia del modo oscuro
    fun cambiarModoOscuro(activado: Boolean) {
        viewModelScope.launch {
            preferenciasUsuario.guardarModoOscuro(activado)
        }
    }

    // ====================================================================
    // AQUÍ MANTIENES TUS FUNCIONES Y ESTADOS PREVIOS (Catálogo, Favoritos, etc.)
    // ====================================================================
}