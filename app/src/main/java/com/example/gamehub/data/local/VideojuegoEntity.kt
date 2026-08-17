package com.example.gamehub.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ENTIDAD DE ROOM PARA LA TABLA DE FAVORITOS
 * Mantiene la persistencia de los videojuegos marcados como favoritos.
 * Incluye todos los metadatos necesarios para renderizar el detalle completo sin depender de la red.
 */
@Entity(tableName = "tabla_favoritos")
data class VideojuegoEntity(
    @PrimaryKey
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val genero: String,
    val imagen: String,
    // CAMPOS ADICIONALES: Aseguran que la información completa esté disponible al abrir desde favoritos
    val developer: String? = null,
    val fechaLanzamiento: String? = null,
    val platform: String? = null
)