package com.jventrib.formulainfo.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.jventrib.formulainfo.data.RaceRepository
import com.jventrib.formulainfo.model.db.Session
import com.jventrib.formulainfo.model.db.Session.FP1
import com.jventrib.formulainfo.model.db.Session.FP2
import com.jventrib.formulainfo.model.db.Session.FP3
import com.jventrib.formulainfo.model.db.Session.QUAL
import com.jventrib.formulainfo.model.db.Session.RACE
import com.jventrib.formulainfo.model.db.Session.SPRINT
import com.jventrib.formulainfo.ui.common.formatDateTime
import com.jventrib.formulainfo.ui.preferences.StorePreference
import com.jventrib.formulainfo.ui.preferences.dataStore
import com.jventrib.formulainfo.utils.currentYear
import com.jventrib.formulainfo.utils.now
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import logcat.logcat
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: RaceRepository
) {
    suspend fun notifyNextRaces() {

        val dataStore = context.dataStore.data.first()

        val notifyFirstRun = dataStore[StorePreference.NOTIFY_FIRST_RUN] ?: true
        val notifyFP = dataStore[StorePreference.NOTIFY_PRACTICE] ?: false
        val notifyQual = dataStore[StorePreference.NOTIFY_QUAL] ?: false
        val notifyRace = dataStore[StorePreference.NOTIFY_RACE] ?: true
        val notifyBefore = dataStore[StorePreference.NOTIFY_BEFORE]?.calcNotifyBefore() ?: 10

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextRace = repository.getRaces(currentYear(), false).first()
            .firstOrNull {
                it.raceInfo.sessions.race.minus(notifyBefore, ChronoUnit.MINUTES)
                    .isAfter(now())
            }
        nextRace?.let { r ->

            // Get next session
            val sessions: Map<Instant, Session> = r.raceInfo.sessions.run {
                buildMap {
                    if (fp1 != null && notifyFP) put(fp1, FP1)
                    if (fp2 != null && notifyFP) put(fp2, FP2)
                    if (fp3 != null && notifyFP) put(fp3, FP3)
                    if (qualifying != null && notifyQual) put(qualifying, QUAL)
                    if (sprint != null && notifyRace) put(sprint, SPRINT)
                    if (notifyRace) put(race, RACE)
                }
            }
            val nextSession =
                sessions.entries
                    .firstOrNull {
                        it.key.minus(notifyBefore, ChronoUnit.MINUTES).isAfter(now())
                    }
                    ?.toPair()
                    ?.let { it.first to it.second }
            nextSession?.let { (instant, session) ->

                val withDelay = instant.minus(notifyBefore, ChronoUnit.MINUTES)
                val date = withDelay.toEpochMilli()
                // val date = Instant.now().plusSeconds(10 * r.raceInfo.round.toLong()).toEpochMilli()
                val intent = Intent(context, SessionNotificationReceiver::class.java)
                intent.putExtra("race_name", r.raceInfo.raceName)
                intent.putExtra("race_session", session.label)
                intent.putExtra("session_datetime", instant)
                intent.putExtra("circuit_name", r.circuit.name)
                intent.putExtra("race_flag", r.circuit.location.flag)
                intent.addCategory("android.intent.category.DEFAULT")

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                    } else {
                        PendingIntent.FLAG_CANCEL_CURRENT
                    }
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            date,
                            pendingIntent
                        )
                    } else {
                        if (notifyFirstRun) {
                            Toast.makeText(
                                context,
                                "Please allow Formula Info to send Alarms and reminders to get notified about sessions",
                                Toast.LENGTH_LONG
                            ).show()

                            startActivity(
                                context,
                                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                ),
                                null
                            )
                            context.dataStore.updateData {
                                it.toMutablePreferences()
                                    .apply { set(StorePreference.NOTIFY_FIRST_RUN, false) }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Alarms and reminders not allowed",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, date, pendingIntent)
                }
                logcat { "Setting notification: ${r.raceInfo.raceName} ${session.label} -> ${withDelay.formatDateTime()}" }
            }
        }
    }
}

fun Float.calcNotifyBefore() = toLong() * 5
