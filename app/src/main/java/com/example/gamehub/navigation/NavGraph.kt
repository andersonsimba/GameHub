package com.example.gamehub.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gamehub.presentation.PantallaAjustes
import com.example.gamehub.presentation.PantallaBusqueda
import com.example.gamehub.presentation.PantallaCatalogo
import com.example.gamehub.presentation.PantallaDetalle
import com.example.gamehub.presentation.PantallaFavoritos
import com.example.gamehub.presentation.PantallaInicio
import com.example.gamehub.presentation.PantallaPerfil
import com.example.gamehub.presentation.VideojuegoViewModel

/**
 * GRAFO DE NAVEGACIÓN PRINCIPAL
 * Gestiona el flujo de pantallas, la barra inferior y la pantalla de inicio dinámica
 * basada en la existencia del usuario registrado en DataStore.
 */
@Composable
fun NavGraph(
    viewModel: VideojuegoViewModel
) {
    // Controller principal para gestionar la navegación entre pantallas
    val navController = rememberNavController()

    // OBSERVACIÓN DE DATASTORE: Obtiene el nombre guardado para decidir el destino inicial
    val nombreGuardado by viewModel.nombreUsuario.collectAsState()

    // LÓGICA DE DESTINO INICIAL DINÁMICO:
    // Si ya existe un nombre registrado, inicia directamente en el catálogo.
    val destinoInicial = if (nombreGuardado.trim().isNotEmpty()) "catalogo" else "inicio"

    // Obtiene la ruta activa para destacar el ícono correspondiente en la barra
    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = currentRoute == "inicio",
                    onClick = { navController.navigate("inicio") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Inicio"
                        )
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == "catalogo",
                    onClick = { navController.navigate("catalogo") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.VideogameAsset,
                            contentDescription = "Catálogo"
                        )
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == "busqueda",
                    onClick = { navController.navigate("busqueda") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Búsqueda"
                        )
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == "favoritos",
                    onClick = { navController.navigate("favoritos") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favoritos"
                        )
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == "perfil",
                    onClick = { navController.navigate("perfil") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil"
                        )
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == "ajustes",
                    onClick = { navController.navigate("ajustes") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes"
                        )
                    }
                )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = destinoInicial, // Asignación dinámica según DataStore
            modifier = Modifier.padding(paddingValues)
        ) {

            // 1. PANTALLA INICIO
            composable("inicio") {
                PantallaInicio(
                    viewModel = viewModel,
                    onIrCatalogo = {
                        // Navega al catálogo y elimina 'inicio' del historial para evitar volver atrás
                        navController.navigate("catalogo") {
                            popUpTo("inicio") { inclusive = true }
                        }
                    }
                )
            }

            // 2. PANTALLA CATÁLOGO
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
                PantallaBusqueda(
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

            // 4. PANTALLA FAVORITOS CON NAVEGACIÓN A DETALLE
            composable("favoritos") {
                PantallaFavoritos(
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

            // 5. PANTALLA DETALLE CON EXTRACCIÓN SEGURA DE PARÁMETROS
            composable(
                route = "detalle/{id}/{nombre}/{descripcion}/{genero}/{developer}/{fechaLanzamiento}/{platform}/{imagen}",
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                    navArgument("nombre") { type = NavType.StringType },
                    navArgument("descripcion") { type = NavType.StringType },
                    navArgument("genero") { type = NavType.StringType },
                    navArgument("developer") { type = NavType.StringType },
                    navArgument("fechaLanzamiento") { type = NavType.StringType },
                    navArgument("platform") { type = NavType.StringType },
                    navArgument("imagen") { type = NavType.StringType }
                )
            ) { backStackEntry ->

                val id = backStackEntry.arguments?.getInt("id") ?: 0
                val nombre = Uri.decode(backStackEntry.arguments?.getString("nombre") ?: "")
                val descripcion = Uri.decode(backStackEntry.arguments?.getString("descripcion") ?: "")
                val genero = Uri.decode(backStackEntry.arguments?.getString("genero") ?: "")
                val developer = Uri.decode(backStackEntry.arguments?.getString("developer") ?: "")
                val fechaLanzamiento = Uri.decode(backStackEntry.arguments?.getString("fechaLanzamiento") ?: "")
                val platform = Uri.decode(backStackEntry.arguments?.getString("platform") ?: "")
                val imagen = Uri.decode(backStackEntry.arguments?.getString("imagen") ?: "")

                PantallaDetalle(
                    id = id,
                    nombre = nombre,
                    descripcion = descripcion,
                    genero = genero,
                    developer = developer,
                    fechaLanzamiento = fechaLanzamiento,
                    platform = platform,
                    imagen = imagen,
                    viewModel = viewModel
                )
            }

            // 6. PANTALLA PERFIL
            composable("perfil") {
                PantallaPerfil(viewModel = viewModel)
            }

            // 7. PANTALLA AJUSTES
            composable("ajustes") {
                PantallaAjustes(viewModel = viewModel)
            }
        }
    }
}