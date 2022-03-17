package com.jventrib.formulainfo.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.model.db.Session.FP1
import com.jventrib.formulainfo.model.db.Session.FP2
import com.jventrib.formulainfo.model.db.Session.FP3
import com.jventrib.formulainfo.model.db.Session.QUAL
import com.jventrib.formulainfo.model.db.Session.RACE
import com.jventrib.formulainfo.ui.preferences.PreferencesKeys
import com.jventrib.formulainfo.ui.preferences.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.schnettler.datastore.manager.DataStoreManager
import java.time.Instant
import java.time.Year
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import logcat.logcat

@HiltViewModel
class NotificationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: RaceRepository
) : ViewModel() {

    suspend fun notifyNextRaces() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextRace =
            repository.getRaces(Year.now().value, false).first().firstOrNull { it.nextRace }
        nextRace?.let { race ->

            // Get next session
            val sessions = race.raceInfo.sessions.run {
                mapOf(
                    fp1 to FP1,
                    fp2 to FP2,
                    fp3 to FP3,
                    qualifying to QUAL,
                    // sprint to SPRINT,
                    this.race to RACE,
                )
            }
            val nextSession =
                sessions.entries
                    .firstOrNull { it.key?.isAfter(Instant.now()) ?: false }
                    ?.toPair()
                    ?.let { it.first!! to it.second }
            nextSession?.let { (instant, session) ->

                val dataStoreManager = DataStoreManager(context.dataStore)

                val notifyBefore: Float = dataStoreManager.getPreference(PreferencesKeys.notifyBefore)

                val date = instant.minus(notifyBefore.toLong(), ChronoUnit.MINUTES).toEpochMilli()
                // val date = Instant.now().plusSeconds(10 * race.raceInfo.round.toLong()).toEpochMilli()
                logcat { "Creating notification for ${race.raceInfo.raceName}: $date" }
                val intent = Intent(context, NotificationReceiver::class.java)
                intent.putExtra("race_name", race.raceInfo.raceName)
                intent.putExtra("race_session", session.label)
                intent.putExtra("session_datetime", instant)
                intent.putExtra("circuit_name", race.circuit.name)
                intent.putExtra("race_flag", race.circuit.location.flag)

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    race.raceInfo.round,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        date,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, date, pendingIntent)
                }
            }
        }
    }
}
