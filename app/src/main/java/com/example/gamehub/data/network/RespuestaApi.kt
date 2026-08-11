package com.example.gamehub.data.network

import com.example.gamehub.data.network.VideojuegoApi

data class RespuestaApi(
    // Lista de videojuegos recibida desde RAWG
    val results: List<VideojuegoApi>
)