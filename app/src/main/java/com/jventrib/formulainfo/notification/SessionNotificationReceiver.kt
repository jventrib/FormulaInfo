package com.jventrib.formulainfo.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Scale
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.ui.common.formatTime
import com.jventrib.formulainfo.utils.Padding
import com.jventrib.formulainfo.utils.now
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

@AndroidEntryPoint
class SessionNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var imageLoader: ImageLoader

//    @Inject
    lateinit var sessionNotificationManager: SessionNotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        if ((Intent.ACTION_BOOT_COMPLETED) != intent.action) {
//            sendNotification(intent, context)
        }
        CoroutineScope(Dispatchers.IO).launch {
//            sessionNotificationManager.notifyNextRaces()
        }
    }

    // private fun sendNotification(intent: Intent, context: Context) {
    //     // perform your scheduled task here (eg. send alarm notification)
    //     val raceName = intent.extras?.getString("race_name") ?: "Not Found"
    //     val sessionDateTime = intent.extras?.get("session_datetime") as Instant?
    //     val flag = intent.extras?.getString("race_flag")
    //     val session = intent.extras?.getString("race_session")
    //
    //     val channelID = "FormulaInfoChannelID"
    //     val channelName = "Formula Info Notifications"
    //     val notificationManager =
    //         getSystemService(context, NotificationManager::class.java)!!
    //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    //         val channel =
    //             NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
    //         notificationManager.createNotificationChannel(channel)
    //     }
    //
    //     CoroutineScope(Dispatchers.IO).launch {
    //         val image = (
    //             imageLoader.execute(
    //                 ImageRequest.Builder(context).data(flag)
    //                     .scale(Scale.FIT)
    //                     .transformations(listOf(Padding(12f, 12f)))
    //                     .build()
    //             ).drawable as BitmapDrawable?
    //             )?.bitmap
    //         val builder =
    //             NotificationCompat.Builder(context.applicationContext, channelID)
    //                 .setContentTitle(raceName)
    //                 .setContentText(
    //                     "$session starting in ${
    //                     java.time.Duration.between(
    //                         now(),
    //                         sessionDateTime
    //                     ).toKotlinDuration().plus(1.seconds).inWholeMinutes
    //                     } minutes (${sessionDateTime?.formatTime()})"
    //                 )
    //                 .setSmallIcon(R.drawable.ic_launcher_notif)
    //                 .setLargeIcon(image)
    //                 .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
    //                 .setPriority(NotificationCompat.PRIORITY_MAX)
    //
    //         notificationManager.notify(1, builder.build())
    //     }
    // }
}
