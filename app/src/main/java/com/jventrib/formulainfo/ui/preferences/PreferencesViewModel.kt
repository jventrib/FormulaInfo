package com.jventrib.formulainfo.ui.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel

// @HiltViewModel
class PreferencesViewModel : ViewModel() {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
}
