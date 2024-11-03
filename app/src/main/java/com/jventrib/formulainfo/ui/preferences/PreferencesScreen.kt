package com.jventrib.formulainfo.ui.preferences

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
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
    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        notify(datastore, preferencesViewModel, false)
    }
    PreferencesScreen(datastore) {
        scope.launch { notify(datastore, preferencesViewModel, true) }
    }
}

private suspend fun notify(
    datastore: StorePreference,
    preferencesViewModel: PreferencesViewModel,
    testRun: Boolean
) {
    datastore.dataStore.data.collect {
        preferencesViewModel.sessionNotificationManager.notifyNextRaces(testRun)
        logcat("PreferencesScreen") { "Rescheduling notifications" }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PreferencesScreen(datastore: IStorePreference, onTestRun: () -> Unit = {}) {
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
                mutableFloatStateOf(notifyBeforePref)
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                var activityLaunched by remember { mutableStateOf(false) }
                val startForResult =
                    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                        activityLaunched = !activityLaunched //We are just using this to trigger a recomposition
                        logcat("PreferencesScreen") { "hasActivityLaunched: $activityLaunched" }
                    }

                key(activityLaunched) {
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Text(text = "Alarms and Reminder not allowed. Please press button to allow")
                        Box(
                            contentAlignment = androidx.compose.ui.Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = {
                                Toast.makeText(
                                    context,
                                    "Please allow Formula Info to send Alarms and reminders to get notified about sessions",
                                    Toast.LENGTH_LONG
                                ).show()

                                startForResult.launch(
                                    //context.startActivity(
                                    Intent(
                                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                        "package:${context.packageName}".toUri()
                                    )
                                )

                            }) { Text(text = "Allow alarms and reminders") }
                        }
                    }
                }
            }

            //ask for permission to send notifications
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val notificationPermissionState = rememberPermissionState(
                    permission = android.Manifest.permission.POST_NOTIFICATIONS
                )
                if (!notificationPermissionState.status.isGranted) {
                    Text(text = "Notifications not allowed. Please press button to allow")
                    Box(
                        contentAlignment = androidx.compose.ui.Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = { notificationPermissionState.launchPermissionRequest() }) {
                            Text(text = "Allow notifications")
                        }
                    }
                }
            }

            Box(
                contentAlignment = androidx.compose.ui.Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    Toast.makeText(
                        context,
                        "Sent Notification, it should be shown in your notifications in 2 seconds",
                        Toast.LENGTH_LONG
                    ).show()

                    onTestRun()
                }) { Text(text = "Test notification") }
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
