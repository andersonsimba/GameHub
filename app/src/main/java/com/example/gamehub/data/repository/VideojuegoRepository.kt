package com.example.gamehub.data.repository

import com.example.gamehub.data.local.VideojuegoDao
import com.example.gamehub.data.local.VideojuegoEntity
import com.example.gamehub.data.network.RetrofitClient
import com.example.gamehub.data.network.VideojuegoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VideojuegoRepository(
    private val dao: VideojuegoDao
) {
    // Obtiene los videojuegos desde la API mediante Retrofit
    suspend fun obtenerVideojuegos(): List<VideojuegoApi> {
        return RetrofitClient.api.obtenerVideojuegos()
    }

    // Observa los favoritos almacenados localmente en Room
    val favoritos: Flow<List<VideojuegoEntity>> = dao.obtenerTodosLosFavoritos()

    // Ejecuta la escritura de Room en el hilo de entrada/salida
    suspend fun guardarFavorito(juego: VideojuegoEntity) {
        withContext(Dispatchers.IO) {
            dao.guardarFavorito(juego)
        }
    }

    // Ejecuta la eliminación de Room en el hilo de entrada/salida
    suspend fun eliminarFavorito(juego: VideojuegoEntity) {
        withContext(Dispatchers.IO) {
            dao.eliminarFavorito(juego)
        }
    }
}