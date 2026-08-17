package com.example.gamehub.data.repository

import com.example.gamehub.data.local.ResenaEntity
import com.example.gamehub.data.local.VideojuegoDao
import com.example.gamehub.data.local.VideojuegoEntity
import com.example.gamehub.data.network.RetrofitClient
import com.example.gamehub.data.network.VideojuegoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VideojuegoRepository(private val dao: VideojuegoDao) {

    // Obtención de videojuegos desde la API remota (Retrofit)
    suspend fun obtenerVideojuegos(): List<VideojuegoApi> {
        return RetrofitClient.api.obtenerVideojuegos()
    }

    // Variable 'favoritos' que tu ViewModel lee exactamente en: val listaFavoritos = repository.favoritos
    val favoritos: Flow<List<VideojuegoEntity>> = dao.obtenerTodosLosFavoritos()

    // Operaciones de Favoritos en Room
    suspend fun guardarFavorito(juego: VideojuegoEntity) {
        withContext(Dispatchers.IO) {
            dao.guardarFavorito(juego)
        }
    }

    suspend fun eliminarFavorito(juego: VideojuegoEntity) {
        withContext(Dispatchers.IO) {
            dao.eliminarFavorito(juego)
        }
    }

    // Operaciones de Reseñas en Room requeridas por tu ViewModel
    suspend fun obtenerResena(idVideojuego: Int): ResenaEntity? {
        return withContext(Dispatchers.IO) {
            dao.obtenerResena(idVideojuego)
        }
    }

    suspend fun guardarResena(resena: ResenaEntity) {
        withContext(Dispatchers.IO) {
            dao.guardarResena(resena)
        }
    }

    suspend fun eliminarResena(idVideojuego: Int) {
        withContext(Dispatchers.IO) {
            dao.eliminarResena(idVideojuego)
        }
    }
}