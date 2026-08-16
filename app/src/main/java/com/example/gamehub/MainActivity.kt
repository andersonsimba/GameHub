package com.example.gamehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.gamehub.data.local.AppDatabase
import com.example.gamehub.data.local.PreferenciasUsuario
import com.example.gamehub.data.repository.VideojuegoRepository
import com.example.gamehub.navigation.NavGraph
import com.example.gamehub.presentation.VideojuegoViewModel
import com.example.gamehub.ui.theme.GameHubTheme

class MainActivity : ComponentActivity() {

    // Inicialización compartida del ViewModel pasando Repository y PreferenciasUsuario (DataStore)
    private val viewModel: VideojuegoViewModel by viewModels {
        VideojuegoViewModel.Factory(
            VideojuegoRepository(
                AppDatabase.getDatabase(applicationContext).videojuegoDao()
            ),
            PreferenciasUsuario(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Observa en tiempo real los cambios guardados en DataStore
            val esModoOscuro by viewModel.esModoOscuro.collectAsState()

            // Aplica el tema dinámico a toda la aplicación
            GameHubTheme(darkTheme = esModoOscuro) {
                NavGraph(viewModel = viewModel)
            }
        }
    }
}