package com.acacioswork.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "acacioswork_prefs")

class SessionManager private constructor(context: Context) {
    private val appContext = context.applicationContext

    companion object {
        @Volatile
        private var INSTANCE: SessionManager? = null
        var token: String? = null
        var userFullName: String? = null
        var userRole: String? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionManager(context)
                INSTANCE = instance
                instance
            }
        }
    }

    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_ROLE_KEY = stringPreferencesKey("user_role")

    init {
        // Inicializar los valores en memoria al crear la instancia de forma síncrona/bloqueante
        try {
            runBlocking {
                token = getToken().first()
                userFullName = getUserName().first()
                userRole = getUserRole().first()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveSession(jwtToken: String, name: String, role: String) {
        token = jwtToken
        userFullName = name
        userRole = role
        appContext.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = jwtToken
            preferences[USER_NAME_KEY] = name
            preferences[USER_ROLE_KEY] = role
        }
    }

    suspend fun clearSession() {
        token = null
        userFullName = null
        userRole = null
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    fun getToken(): Flow<String?> {
        return appContext.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    fun getUserName(): Flow<String?> {
        return appContext.dataStore.data.map { preferences ->
            preferences[USER_NAME_KEY]
        }
    }

    fun getUserRole(): Flow<String?> {
        return appContext.dataStore.data.map { preferences ->
            preferences[USER_ROLE_KEY]
        }
    }
}
