package com.jventrib.formulainfo.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.jventrib.formulainfo.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if ((Intent.ACTION_BOOT_COMPLETED) == intent.action) {
            // reset all alarms
        } else {
            // perform your scheduled task here (eg. send alarm notification)
            val raceName = intent.extras?.get("race_name") as String? ?: "Not Found"
            Toast.makeText(
                context,
                raceName,
                Toast.LENGTH_LONG
            ).show()

            val channelID = "channelID"
            val channelName = "Channel Name"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
                val notificationManager =
                    getSystemService(context, NotificationManager::class.java)!!
                notificationManager.createNotificationChannel(channel)

                val builder =
                    NotificationCompat.Builder(context.getApplicationContext(), channelID)
                        .setContentTitle(raceName)
                        .setContentText("Body of the notification")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)

                notificationManager.notify(1, builder.build())
            }
        }
    }
}
