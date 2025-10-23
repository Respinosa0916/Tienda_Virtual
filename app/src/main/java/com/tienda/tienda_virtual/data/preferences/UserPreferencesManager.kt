package com.tienda.tienda_virtual.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property para crear el DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesManager(private val context: Context) {
    
    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_TYPE_KEY = stringPreferencesKey("user_type")
        private val USER_PHONE_KEY = stringPreferencesKey("user_phone")
        private val USER_ADDRESS_KEY = stringPreferencesKey("user_address")
    }
    
    // Guardar usuario
    suspend fun saveUser(
        userId: String,
        email: String,
        name: String,
        userType: String,
        phone: String?,
        address: String?
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_NAME_KEY] = name
            preferences[USER_TYPE_KEY] = userType
            phone?.let { preferences[USER_PHONE_KEY] = it }
            address?.let { preferences[USER_ADDRESS_KEY] = it }
        }
    }
    
    // Obtener ID del usuario
    val userId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }
    
    // Obtener email del usuario
    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL_KEY]
    }
    
    // Obtener nombre del usuario
    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY]
    }
    
    // Obtener tipo de usuario
    val userType: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_TYPE_KEY]
    }
    
    // Obtener teléfono del usuario
    val userPhone: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_PHONE_KEY]
    }
    
    // Obtener dirección del usuario
    val userAddress: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ADDRESS_KEY]
    }
    
    // Limpiar datos del usuario (logout)
    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    // Verificar si hay usuario guardado
    val hasUser: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY] != null
    }
}

