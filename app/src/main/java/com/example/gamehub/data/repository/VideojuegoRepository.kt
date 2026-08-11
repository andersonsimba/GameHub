package com.example.gamehub.data.repository

import com.example.gamehub.data.local.VideojuegoDao
import com.example.gamehub.data.local.VideojuegoEntity
import com.example.gamehub.data.network.RetrofitClient
import com.example.gamehub.data.network.VideojuegoApi
import kotlinx.coroutines.flow.Flow

class VideojuegoRepository(
    private val dao: VideojuegoDao
) {
    // OBTENCIÓN DE DATOS DE LA API REST (RETROFIT)
    suspend fun obtenerVideojuegos(): List<VideojuegoApi> {
        return RetrofitClient.api.obtenerVideojuegos()
    }

    // GESTIÓN DE FAVORITOS EN BASE DE DATOS LOCAL (ROOM)
    val favoritos: Flow<List<VideojuegoEntity>> = dao.obtenerTodosLosFavoritos()

    fun guardarFavorito(juego: VideojuegoEntity) {
        dao.guardarFavorito(juego)
    }

    fun eliminarFavorito(juego: VideojuegoEntity) {
        dao.eliminarFavorito(juego)
    }
}