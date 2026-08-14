package com.example.gamehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.gamehub.data.local.AppDatabase
import com.example.gamehub.data.repository.VideojuegoRepository
import com.example.gamehub.navigation.NavGraph
import com.example.gamehub.presentation.VideojuegoViewModel
import com.example.gamehub.ui.theme.GameHubTheme

class MainActivity : ComponentActivity() {

    private val viewModel: VideojuegoViewModel by viewModels {
        VideojuegoViewModel.Factory(
            VideojuegoRepository(
                AppDatabase.getDatabase(applicationContext).videojuegoDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val esModoOscuro = viewModel.esModoOscuro

            GameHubTheme(darkTheme = esModoOscuro) {
                NavGraph()
            }
        }
    }

}