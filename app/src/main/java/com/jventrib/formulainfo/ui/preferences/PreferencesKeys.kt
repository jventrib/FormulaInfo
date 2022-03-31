package com.jventrib.formulainfo.ui.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import de.schnettler.datastore.manager.PreferenceRequest

object PreferencesKeys {
    val notifyFP = PreferenceRequest(booleanPreferencesKey("notify_fp"), false)
    val notifyQual = PreferenceRequest(booleanPreferencesKey("notify_qual"), false)
    val notifyRace = PreferenceRequest(booleanPreferencesKey("notify_race"), true)
    val notifyBefore = PreferenceRequest(floatPreferencesKey("notify_before"), 10f)
}
