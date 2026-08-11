package com.example.gamehub.data.network

import com.google.gson.annotations.SerializedName

data class VideojuegoApi(
    val id: Int,

    @SerializedName("title")
    val nombre: String,

    @SerializedName("short_description")
    val descripcion: String?,

    @SerializedName("genre")
    val genero: String?,

    val developer: String?,

    @SerializedName("release_date")
    val fechaLanzamiento: String?,

    val platform: String?,

    @SerializedName("thumbnail")
    val imagen: String?
)