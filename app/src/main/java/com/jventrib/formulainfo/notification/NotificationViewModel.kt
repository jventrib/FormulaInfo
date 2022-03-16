package com.jventrib.formulainfo.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import com.jventrib.formulainfo.data.RaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.Year
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import logcat.logcat

@HiltViewModel
class NotificationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: RaceRepository
) :
    ViewModel() {

    suspend fun notifyNextRaces() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val races = repository.getRaces(Year.now().value, false).first()
        races.forEach { race ->
            val date = Instant.now().plusSeconds(30 * race.raceInfo.round.toLong()).toEpochMilli()
            logcat { "Creating notification for ${race.raceInfo.raceName}: $date" }
            val intent = Intent(context, NotificationReceiver::class.java)
            intent.putExtra("race_name", race.raceInfo.raceName)
            // intent.putExtra("race_datetime", race.raceInfo.sessions.race)
            // intent.putExtra("circuit_name", race.circuit.name)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                race.raceInfo.round,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, date, pendingIntent)
            }
        }
    }
}
