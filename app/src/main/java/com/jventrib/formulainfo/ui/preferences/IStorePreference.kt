package com.jventrib.formulainfo.ui.preferences

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface IStorePreference {
    // get the saved email
    fun <T> getPreferenceItem(key: Preferences.Key<T>, default: T): Flow<T>

    // save email into datastore
    suspend fun <T> savePreferenceItem(key: Preferences.Key<T>, name: T)
}
