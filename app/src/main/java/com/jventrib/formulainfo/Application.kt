package com.jventrib.formulainfo

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import logcat.AndroidLogcatLogger
import logcat.LogPriority

//import leakcanary.LeakCanary

@ExperimentalCoroutinesApi
@FlowPreview
@HiltAndroidApp
class Application: Application() {

    override fun onCreate() {
        super.onCreate()
//        LeakCanary.config = LeakCanary.config.copy(retainedVisibleThreshold = 1)

        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
    }

}