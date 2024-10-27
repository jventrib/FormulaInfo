package com.jventrib.formulainfo

import android.app.Application
import com.jventrib.formulainfo.notification.SessionNotificationManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.logcat
import javax.inject.Inject

// import leakcanary.LeakCanary

@ExperimentalCoroutinesApi
@FlowPreview
@HiltAndroidApp
class Application : Application() {

//    @Inject
//    lateinit var sessionNotificationManager: SessionNotificationManager

    override fun onCreate() {
        super.onCreate()
//        LeakCanary.config = LeakCanary.config.copy(retainedVisibleThreshold = 1)

        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.DEBUG)
        MainScope().launch {
            logcat { "Init application" }
//            sessionNotificationManager.notifyNextRaces()
        }
    }
}
