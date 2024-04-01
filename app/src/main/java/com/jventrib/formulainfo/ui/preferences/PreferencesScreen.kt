package com.jventrib.formulainfo.ui.preferences

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jventrib.formulainfo.notification.calcNotifyBefore
import com.jventrib.formulainfo.ui.common.toDurationString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import logcat.logcat

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

fun NavGraphBuilder.preference() {
    composable("preference") { PreferencesScreen() }
}

@OptIn(ExperimentalMaterialApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
private fun PreferencesScreen() {
    val preferencesViewModel: PreferencesViewModel = hiltViewModel()
    val dataStore = LocalContext.current.dataStore
    val datastore = StorePreference(dataStore)

    LaunchedEffect(Unit) {
        datastore.dataStore.data.collect {
            preferencesViewModel.sessionNotificationManager.notifyNextRaces()
            logcat { "Rescheduling notifications" }
        }
    }

    PreferencesScreen(datastore)
}

@Composable
private fun PreferencesScreen(datastore: IStorePreference) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Preferences") }) }
    ) {
        val context = LocalContext.current
        Column(
            Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            PreferenceSwitch(
                datastore,
                scope,
                "Free practice",
                StorePreference.NOTIFY_PRACTICE,
                false
            )
            PreferenceSwitch(datastore, scope, "Qualification", StorePreference.NOTIFY_QUAL, false)
            PreferenceSwitch(datastore, scope, "Race", StorePreference.NOTIFY_RACE, true)
            Spacer(modifier = Modifier.height(16.dp))

            val notifyBeforePref =
                datastore.getPreferenceItem(StorePreference.NOTIFY_BEFORE, 10f)
                    .collectAsState(initial = 10f).value
            var tempValue by remember(notifyBeforePref) {
                mutableStateOf(notifyBeforePref)
            }
            Text(text = "Notify before:")
            Spacer(modifier = Modifier.height(0.dp))
            Text(
                text = tempValue.calcNotifyBefore().toDurationString(),
                modifier = Modifier.offset(
                    lerp(
                        24.dp,
                        LocalConfiguration.current.screenWidthDp.dp - 24.dp,
                        tempValue / 24f
                    ) - 36.dp
                )
            )
            Slider(
                value = tempValue,
                onValueChange = { tempValue = it },
                valueRange = 0f..24f,
                steps = 24,
                onValueChangeFinished = {
                    scope.launch {
                        datastore.savePreferenceItem(
                            StorePreference.NOTIFY_BEFORE,
                            tempValue
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(30.dp))
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {

                Text(text = "Alarms and Reminder not allowed. Please press button to allow")
                Button(onClick = {
                    Toast.makeText(
                        context,
                        "Please allow Formula Info to send Alarms and reminders to get notified about sessions",
                        Toast.LENGTH_LONG
                    ).show()

                    ContextCompat.startActivity(
                        context,
                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                        ),
                        null
                    )

                }) {
                    Text(text = "Allow")
                }
            }
        }
    }
}

@Composable
private fun PreferenceSwitch(
    datastore: IStorePreference,
    scope: CoroutineScope,
    text: String,
    key: Preferences.Key<Boolean>,
    default: Boolean
) {
    Row(Modifier.fillMaxWidth()) {
        Text(
            text,
            Modifier
                .align(CenterVertically)
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = datastore.getPreferenceItem(
                key,
                default,
            ).collectAsState(default).value,
            onCheckedChange = { value ->
                scope.launch {
                    datastore.savePreferenceItem(
                        key,
                        value
                    )
                }
            },
        )
    }
}

@Preview
@Composable
fun PreferencesScreenPreview() {
    PreferencesScreen(FakeStorePreference())
}
