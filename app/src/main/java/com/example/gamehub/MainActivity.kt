package com.example.gamehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gamehub.navigation.NavGraph
import com.example.gamehub.ui.theme.GameHubTheme

// ACTIVIDAD PRINCIPAL: Punto de entrada único de la aplicación (Single Activity Architecture)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el árbol de componentes gráficos de Jetpack Compose
        setContent {
            // Aplica el tema de diseño global de la aplicación (colores, tipografía y formas)
            GameHubTheme {
                // Invoca al contenedor de navegación principal
                NavGraph()
            }
        }
    }
}