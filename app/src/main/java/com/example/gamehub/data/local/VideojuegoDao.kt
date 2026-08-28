package com.example.gamehub.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideojuegoDao {
    @Query("SELECT * FROM tabla_favoritos")
    fun obtenerTodosLosFavoritos(): Flow<List<VideojuegoEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardarFavorito(videojuego: VideojuegoEntity)
    @Delete
    fun eliminarFavorito(videojuego: VideojuegoEntity)
    @Query("SELECT * FROM tabla_resenas WHERE idVideojuego = :idVideojuego LIMIT 1")
    fun obtenerResena(idVideojuego: Int): ResenaEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardarResena(resena: ResenaEntity)
    @Query("DELETE FROM tabla_resenas WHERE idVideojuego = :idVideojuego")
    fun eliminarResena(idVideojuego: Int)
}
