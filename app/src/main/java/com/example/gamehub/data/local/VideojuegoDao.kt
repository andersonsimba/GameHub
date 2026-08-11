package com.example.gamehub.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// DAO (Data Access Object): Define las operaciones sobre la base de datos de Room
@Dao
interface VideojuegoDao {

    // Consulta reactiva para listar favoritos en tiempo real con Flow
    @Query("SELECT * FROM tabla_favoritos")
    fun obtenerTodosLosFavoritos(): Flow<List<VideojuegoEntity>>

    // Inserción de registro local (sin suspend para evitar conflicto de firmas en KSP)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardarFavorito(videojuego: VideojuegoEntity)

    // Eliminación de registro local (sin suspend para evitar conflicto de firmas en KSP)
    @Delete
    fun eliminarFavorito(videojuego: VideojuegoEntity)
}