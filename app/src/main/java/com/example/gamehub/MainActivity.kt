package com.example.gamehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gamehub.navigation.NavGraph
import com.example.gamehub.ui.theme.GameHubTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            GameHubTheme {

                NavGraph()

            }
        }
    }
}