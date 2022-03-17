package com.jventrib.formulainfo.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Scale
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.ui.common.format
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onReceive(context: Context, intent: Intent) {
        if ((Intent.ACTION_BOOT_COMPLETED) == intent.action) {
            // reset all alarms
        } else {
            // perform your scheduled task here (eg. send alarm notification)
            val raceName = intent.extras?.get("race_name") as String? ?: "Not Found"
            val raceDateTime = intent.extras?.get("race_datetime") as Instant?
            val flag = intent.extras?.get("race_flag") as String?
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

                CoroutineScope(Dispatchers.IO).launch {
                    val image = (
                        imageLoader.execute(
                            ImageRequest.Builder(context).data(flag)
                                .scale(Scale.FIT)
                                .build()
                        ).drawable as BitmapDrawable?
                        )?.bitmap
                    val builder =
                        NotificationCompat.Builder(context.getApplicationContext(), channelID)
                            .setContentTitle(raceName)
                            .setContentText(raceDateTime?.format())
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setLargeIcon(image)

                    notificationManager.notify(1, builder.build())
                }
            }
        }
    }
}
