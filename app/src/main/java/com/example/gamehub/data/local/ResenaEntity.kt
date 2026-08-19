package com.example.gamehub.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad de Room para la tabla de reseñas locales
@Entity(tableName = "tabla_resenas")
data class ResenaEntity(
    @PrimaryKey
    val idVideojuego: Int,
    val nombreVideojuego: String,
    val calificacion: Int,
    val comentario: String
)