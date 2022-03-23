package com.jventrib.formulainfo.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.model.db.Session
import com.jventrib.formulainfo.model.db.Session.FP1
import com.jventrib.formulainfo.model.db.Session.FP2
import com.jventrib.formulainfo.model.db.Session.FP3
import com.jventrib.formulainfo.model.db.Session.QUAL
import com.jventrib.formulainfo.model.db.Session.RACE
import com.jventrib.formulainfo.ui.common.format
import com.jventrib.formulainfo.ui.preferences.PreferencesKeys
import com.jventrib.formulainfo.ui.preferences.PreferencesKeys.notifyBefore
import com.jventrib.formulainfo.ui.preferences.dataStore
import com.jventrib.formulainfo.utils.currentYear
import com.jventrib.formulainfo.utils.now
import dagger.hilt.android.qualifiers.ApplicationContext
import de.schnettler.datastore.manager.DataStoreManager
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import logcat.logcat

@Singleton
class SessionNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: RaceRepository
) {
    suspend fun notifyNextRaces() {
        val dataStoreManager = DataStoreManager(context.dataStore)
        val notifyBefore: Float = dataStoreManager.getPreference(PreferencesKeys.notifyBefore)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextRace =
            repository.getRaces(currentYear(), false).first()
                .firstOrNull {
                    it.raceInfo.sessions.race.minus(notifyBefore.toLong(), ChronoUnit.MINUTES)
                        .isAfter(now())
                }
        nextRace?.let { race ->

            // Get next session
            val sessions: Map<Instant?, Session> = race.raceInfo.sessions.run {
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
                    .firstOrNull {
                        it.key
                            ?.minus(notifyBefore.toLong(), ChronoUnit.MINUTES)
                            ?.isAfter(now()) ?: false
                    }
                    ?.toPair()
                    ?.let { it.first!! to it.second }
            nextSession?.let { (instant, session) ->

                val minus = instant.minus(notifyBefore.toLong(), ChronoUnit.MINUTES)
                val date = minus.toEpochMilli()
                // val date = Instant.now().plusSeconds(10 * race.raceInfo.round.toLong()).toEpochMilli()
                val intent = Intent(context, NotificationReceiver::class.java)
                intent.putExtra("race_name", race.raceInfo.raceName)
                intent.putExtra("race_session", session.label)
                intent.putExtra("session_datetime", instant)
                intent.putExtra("circuit_name", race.circuit.name)
                intent.putExtra("race_flag", race.circuit.location.flag)

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
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
                logcat { "Setting notification: ${race.raceInfo.raceName} ${session.label} -> ${instant.format()}" }
            }
        }
    }
}
