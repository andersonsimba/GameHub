package com.example.gamehub.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gamehub.data.local.AppDatabase
import com.example.gamehub.data.repository.VideojuegoRepository
import com.example.gamehub.presentation.PantallaAjustes
import com.example.gamehub.presentation.PantallaBusqueda
import com.example.gamehub.presentation.PantallaCatalogo
import com.example.gamehub.presentation.PantallaDetalle
import com.example.gamehub.presentation.PantallaFavoritos
import com.example.gamehub.presentation.PantallaInicio
import com.example.gamehub.presentation.PantallaPerfil
import com.example.gamehub.presentation.VideojuegoViewModel

@Composable
fun NavGraph() {
    // Controller principal para gestionar la navegación entre pantallas de Compose
    val navController = rememberNavController()

    // Contexto local necesario para inicializar Room Database
    val context = LocalContext.current

    // Inicialización del Repositorio unificando el DAO de Room con Retrofit
    val database = AppDatabase.getDatabase(context)
    val repository = VideojuegoRepository(database.videojuegoDao())

    // Instanciación del ViewModel compartida entre pantallas mediante ViewModel Factory
    val viewModel: VideojuegoViewModel = viewModel(
    )

    // Obtiene la ruta activa actualmente para destacar el ícono correspondiente en la barra inferior
    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    Scaffold(
        bottomBar = {
            // BARRA DE NAVEGACIÓN INFERIOR (BOTTOM NAVIGATION BAR)
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "inicio",
                    onClick = { navController.navigate("inicio") },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Inicio") }
                )

                NavigationBarItem(
                    selected = currentRoute == "catalogo",
                    onClick = { navController.navigate("catalogo") },
                    icon = { Icon(imageVector = Icons.Default.VideogameAsset, contentDescription = "Catálogo") }
                )

                NavigationBarItem(
                    selected = currentRoute == "busqueda",
                    onClick = { navController.navigate("busqueda") },
                    icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Búsqueda") }
                )

                NavigationBarItem(
                    selected = currentRoute == "favoritos",
                    onClick = { navController.navigate("favoritos") },
                    icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favoritos") }
                )
            }
        }
    ) { paddingValues ->

        // MAPA DE NAVEGACIÓN DE LA APLICACIÓN
        NavHost(
            navController = navController,
            startDestination = "inicio",
            modifier = Modifier.padding(paddingValues)
        ) {

            // 1. PANTALLA INICIO
            composable("inicio") {
                PantallaInicio(
                    onIrCatalogo = {
                        navController.navigate("catalogo")
                    }
                )
            }

            // 2. PANTALLA CATÁLOGO (Consume el ViewModel)
            composable("catalogo") {
                PantallaCatalogo(
                    viewModel = viewModel,
                    onVideojuegoClick = { videojuego ->
                        val ruta = "detalle/" +
                                "${videojuego.id}/" +
                                "${Uri.encode(videojuego.nombre)}/" +
                                "${Uri.encode(videojuego.descripcion ?: "")}/" +
                                "${Uri.encode(videojuego.genero ?: "")}/" +
                                "${Uri.encode(videojuego.developer ?: "")}/" +
                                "${Uri.encode(videojuego.fechaLanzamiento ?: "")}/" +
                                "${Uri.encode(videojuego.platform ?: "")}/" +
                                "${Uri.encode(videojuego.imagen ?: "")}"

                        navController.navigate(ruta)
                    }
                )
            }

            // 3. PANTALLA BÚSQUEDA
            composable("busqueda") {
                PantallaBusqueda(viewModel = viewModel)
            }

            // 4. PANTALLA FAVORITOS (Muestra los datos almacenados en Room)
            composable("favoritos") {
                PantallaFavoritos(viewModel = viewModel)
            }

            // 5. PANTALLA DETALLE (Recibe argumentos por URL)
            composable(
                route = "detalle/{id}/{nombre}/{descripcion}/{genero}/{developer}/{fechaLanzamiento}/{platform}/{imagen}"
            ) { backStackEntry ->
                PantallaDetalle(
                    nombre = backStackEntry.arguments?.getString("nombre") ?: "",
                    descripcion = backStackEntry.arguments?.getString("descripcion") ?: "",
                    genero = backStackEntry.arguments?.getString("genero") ?: "",
                    developer = backStackEntry.arguments?.getString("developer") ?: "",
                    fechaLanzamiento = backStackEntry.arguments?.getString("fechaLanzamiento") ?: "",
                    platform = backStackEntry.arguments?.getString("platform") ?: "",
                    imagen = backStackEntry.arguments?.getString("imagen") ?: ""
                )
            }
            composable("ajustes") {
                PantallaAjustes(viewModel = viewModel)
            }
            composable("perfil") {
                PantallaPerfil()
            }
        }
    }
}