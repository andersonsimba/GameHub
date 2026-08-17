package com.example.gamehub.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad de Room para la tabla de favoritos
@Entity(tableName = "tabla_favoritos")
data class VideojuegoEntity(
    @PrimaryKey
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val genero: String,
    val imagen: String
)