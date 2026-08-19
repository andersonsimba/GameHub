package com.example.gamehub.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para instanciar el DataStore de preferencias
private val Context.dataStore by preferencesDataStore(name = "preferencias_gamehub")

class PreferenciasUsuario(private val context: Context) {

    companion object {
        val CLAVE_MODO_OSCURO = booleanPreferencesKey("modo_oscuro")
        val CLAVE_NOMBRE_USUARIO = stringPreferencesKey("nombre_usuario")
        // Clave para guardar la ruta de la foto de perfil en DataStore
        val CLAVE_FOTO_PERFIL = stringPreferencesKey("foto_perfil_uri")
    }

    // Flujo observable para el modo oscuro
    val modoOscuroFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[CLAVE_MODO_OSCURO] ?: false
    }

    // Flujo observable para el nombre de usuario
    val nombreUsuarioFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CLAVE_NOMBRE_USUARIO] ?: ""
    }

    // Flujo observable para la URI de la foto de perfil
    val fotoPerfilUriFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CLAVE_FOTO_PERFIL] ?: ""
    }

    // Guarda la preferencia de tema oscuro
    suspend fun guardarModoOscuro(esOscuro: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CLAVE_MODO_OSCURO] = esOscuro
        }
    }

    // Guarda el nombre introducido en el perfil
    suspend fun guardarNombreUsuario(nombre: String) {
        context.dataStore.edit { preferences ->
            preferences[CLAVE_NOMBRE_USUARIO] = nombre
        }
    }

    // Guarda la ruta/URI de la foto guardada en almacenamiento interno
    suspend fun guardarFotoPerfilUri(uriString: String) {
        context.dataStore.edit { preferences ->
            preferences[CLAVE_FOTO_PERFIL] = uriString
        }
    }
}