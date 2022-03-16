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
import java.time.LocalDateTime
import java.time.Year
import javax.inject.Inject
import kotlinx.coroutines.flow.first

@HiltViewModel
class NotificationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: RaceRepository
) :
    ViewModel() {

    suspend fun notifyNextRace() {

        val races = repository.getRaces(Year.now().value, false).first()
        val nextRace = races.firstOrNull { it.nextRace }
        nextRace?.let {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NotificationReceiver::class.java)
            intent.putExtra("race_name", it.raceInfo.raceName)
            intent.putExtra("race_datetime", it.raceInfo.sessions.race)
            intent.putExtra("circuit_name", it.circuit.name)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    LocalDateTime.now().plusSeconds(30).second * 1000L,
                    pendingIntent
                )
            }
        }
    }
}
