package com.jventrib.formulainfo.ui.preferences

import android.content.Context
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.jventrib.formulainfo.ui.common.toDurationString
import de.schnettler.datastore.compose.material.PreferenceScreen
import de.schnettler.datastore.compose.material.model.Preference

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@OptIn(ExperimentalMaterialApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun PreferencesScreen() {

    val dataStore = LocalContext.current.dataStore
    // val dataStoreManager = remember { DataStoreManager(dataStore) }
    // val scope = rememberCoroutineScope()
    // val switchPreference by dataStoreManager.getPreferenceFlow(waterRequest)
    //     .collectAsState(initial = false)

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
                            summary = "Notify Free Practice",
                            singleLineTitle = true,
                            icon = {}
                        ),
                        Preference.PreferenceItem.SwitchPreference(
                            PreferencesKeys.notifyQual,
                            "Qualification",
                            summary = "Notify Qualification",
                            singleLineTitle = true,
                            icon = {}
                        ),
                        Preference.PreferenceItem.SwitchPreference(
                            PreferencesKeys.notifyRace,
                            "Race",
                            summary = "Notify Race",
                            singleLineTitle = true,
                            icon = {}
                        ),
                        Preference.PreferenceItem.SeekBarPreference(
                            PreferencesKeys.notifyBefore,
                            "Notify Before",
                            summary = "Minutes",
                            singleLineTitle = true,
                            icon = {},
                            valueRange = 0f..120f,
                            steps = 121,
                            valueRepresentation = { it.toLong().toDurationString() }
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
