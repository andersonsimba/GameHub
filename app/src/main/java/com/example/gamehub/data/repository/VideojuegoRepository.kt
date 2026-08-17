package com.example.gamehub.data.repository

import com.example.gamehub.data.local.ResenaEntity
import com.example.gamehub.data.local.VideojuegoDao
import com.example.gamehub.data.local.VideojuegoEntity
import com.example.gamehub.data.network.RetrofitClient
import com.example.gamehub.data.network.VideojuegoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * REPOSITORIO DE VIDEOJUEGOS
 * Actúa como la única fuente de verdad (Single Source of Truth) mediando
 * entre la API remota (Retrofit) y la base de datos local (Room).
 */
class VideojuegoRepository(private val dao: VideojuegoDao) {

    // CONSULTA API: Obtención de la lista de videojuegos desde la API remota (FreeToGame)
    suspend fun obtenerVideojuegos(): List<VideojuegoApi> {
        return RetrofitClient.api.obtenerVideojuegos()
    }

    // FLUJO CONTINUO: Inserción y observación reactiva de favoritos mediante Flow
    val favoritos: Flow<List<VideojuegoEntity>> = dao.obtenerTodosLosFavoritos()

    // OPERACIONES DE FAVORITOS EN ROOM (Ejecutadas en hilo secundario mediante Dispatchers.IO)
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

    // OPERACIONES DE RESEÑAS EN ROOM
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

// MAPPING / MAPEADORES: Transforman los datos entre la API (red) y la Entidad (Room)
fun VideojuegoApi.aEntity(): VideojuegoEntity {
    return VideojuegoEntity(
        id = id,
        nombre = nombre,
        descripcion = descripcion ?: "",
        genero = genero ?: "",
        imagen = imagen ?: "",
        developer = developer ?: "",
        fechaLanzamiento = fechaLanzamiento ?: "",
        platform = platform ?: ""
    )
}

fun VideojuegoEntity.aApi(): VideojuegoApi {
    return VideojuegoApi(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        genero = genero,
        imagen = imagen,
        developer = developer,
        fechaLanzamiento = fechaLanzamiento,
        platform = platform
    )
}