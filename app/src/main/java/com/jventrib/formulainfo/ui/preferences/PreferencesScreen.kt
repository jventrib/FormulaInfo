package com.jventrib.formulainfo.ui.preferences

import android.content.Context
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.navigation.compose.hiltViewModel
import com.jventrib.formulainfo.notification.calcNotifyBefore
import com.jventrib.formulainfo.ui.common.toDurationString
import de.schnettler.datastore.compose.material.PreferenceScreen
import de.schnettler.datastore.compose.material.model.Preference
import de.schnettler.datastore.manager.DataStoreManager
import kotlinx.coroutines.flow.collect
import logcat.logcat

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@OptIn(ExperimentalMaterialApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun PreferencesScreen() {

    val preferencesViewModel: PreferencesViewModel = hiltViewModel()

    val dataStore = LocalContext.current.dataStore
    val dataStoreManager = remember { DataStoreManager(dataStore) }

    LaunchedEffect(Unit) {
        dataStoreManager.preferenceFlow.collect {
            preferencesViewModel.sessionNotificationManager.notifyNextRaces()
            logcat { "Rescheduling notifications" }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Preferences") }) }
    ) {
        PreferenceScreen(
            items = listOf(
                Preference.PreferenceGroup(
                    title = "Notifications", true,
                    listOf(
                        Preference.PreferenceItem.SwitchPreference(
                            PreferencesKeys.notifyFP,
                            "Free Practice",
                            summary = "",
                            singleLineTitle = true,
                            icon = {}
                        ),
                        Preference.PreferenceItem.SwitchPreference(
                            PreferencesKeys.notifyQual,
                            "Qualification",
                            summary = "",
                            singleLineTitle = true,
                            icon = {}
                        ),
                        Preference.PreferenceItem.SwitchPreference(
                            PreferencesKeys.notifyRace,
                            "Race",
                            summary = "",
                            singleLineTitle = true,
                            icon = {}
                        ),
                        Preference.PreferenceItem.SeekBarPreference(
                            PreferencesKeys.notifyBefore,
                            "Notify Before",
                            summary = "",
                            singleLineTitle = true,
                            icon = {},
                            valueRange = 0f..24f,
                            steps = 24,
                            valueRepresentation = { it.calcNotifyBefore().toDurationString() }
                        ),
                    )
                )
            ),
            dataStore = dataStore,
            statusBarPadding = false
        )
    }
}

@Preview
@Composable
fun PreferencesScreenPreview() {
    PreferencesScreen()
}
