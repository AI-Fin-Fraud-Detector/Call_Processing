package com.talsk.amadz.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    private companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    }

    suspend fun getAccessToken(): String? =
        dataStore.data.map { it[ACCESS_TOKEN_KEY] }.firstOrNull()

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { it[ACCESS_TOKEN_KEY] = token }
    }

    suspend fun clearAccessToken() {
        dataStore.edit { it.remove(ACCESS_TOKEN_KEY) }
    }
}
