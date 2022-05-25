package com.jventrib.formulainfo.ui.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StorePreference(val dataStore: DataStore<Preferences>) : IStorePreference {

    // to make sure there's only one instance
    companion object {
        val NOTIFY_PRACTICE = booleanPreferencesKey("notify_practice")
        val NOTIFY_QUAL = booleanPreferencesKey("notify_qual")
        val NOTIFY_RACE = booleanPreferencesKey("notify_race")
        val NOTIFY_BEFORE = floatPreferencesKey("notify_before")
    }

    // get the saved email
    override fun <T> getPreferenceItem(key: Preferences.Key<T>, default: T): Flow<T> = dataStore.data
        .map { preferences ->
            preferences[key] ?: default
        }

    // save email into datastore
    override suspend fun <T> savePreferenceItem(key: Preferences.Key<T>, name: T) {
        dataStore.edit { preferences ->
            preferences[key] = name
        }
    }
}
