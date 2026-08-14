package com.example.gamehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.gamehub.navigation.NavGraph
import com.example.gamehub.presentation.VideojuegoViewModel
import com.example.gamehub.ui.theme.GameHubTheme

// Actividad principal de la app que hereda de ComponentActivity
class MainActivity : ComponentActivity() {

    // Instancia del ViewModel asociada al ciclo de vida de la Activity
    private val viewModel: VideojuegoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el árbol de componentes gráficos de Jetpack Compose
        setContent {
            // Se observa la preferencia de modo oscuro expuesta desde DataStore
            val esModoOscuro by viewModel.esModoOscuro.collectAsState()

            // Aplica el tema global dinámico
            GameHubTheme(darkTheme = esModoOscuro) {
                // Invocación al contenedor de navegación principal
                NavGraph()
            }
        }
    }
}