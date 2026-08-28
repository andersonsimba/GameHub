package com.example.gamehub.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // URL base utilizada por Retrofit para realizar las solicitudes
    private const val BASE_URL = "https://www.freetogame.com/api/"

    val api: GameApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameApi::class.java)
    }
}


