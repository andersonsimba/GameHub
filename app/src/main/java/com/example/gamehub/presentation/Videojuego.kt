package com.example.gamehub.presentation

data class Videojuego(
    val id: Int,
    val nombre: String,
    val imagenUrl: String,
    val calificacion: Double,
    val genero: String
)