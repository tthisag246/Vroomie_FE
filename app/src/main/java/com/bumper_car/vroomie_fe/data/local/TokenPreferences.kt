package com.bumper_car.vroomie_fe.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "auth")

class TokenPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[TOKEN_KEY] ?: ""}

    suspend fun setToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun getToken(): String {
        return context.dataStore.data.first()[TOKEN_KEY] ?: ""
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }
}
