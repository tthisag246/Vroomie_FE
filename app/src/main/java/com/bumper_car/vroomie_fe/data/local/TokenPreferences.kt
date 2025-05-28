package com.bumper_car.vroomie_fe.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "auth")

object TokenPreferences {
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    fun getToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { it[TOKEN_KEY] }
    }

    suspend fun setToken(context: Context, token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun clearToken(context: Context) {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }
}