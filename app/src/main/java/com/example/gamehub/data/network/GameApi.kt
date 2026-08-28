package com.example.gamehub.data.network

import retrofit2.http.GET

interface GameApi {

    // Obtiene directamente la lista de videojuegos de FreeToGame
    @GET("games")
    suspend fun obtenerVideojuegos(): List<VideojuegoApi>
}
