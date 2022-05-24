package com.jventrib.formulainfo.ui.preferences

import androidx.datastore.preferences.core.Preferences
import de.schnettler.datastore.compose.material.model.Preference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeStorePreference : IStorePreference {
    override fun <T> getPreferenceItem(key: Preferences.Key<T>, default: T): Flow<T> {
        return flowOf(default)
    }

    override suspend fun <T> savePreferenceItem(key: Preferences.Key<T>, name: T) {
    }
}