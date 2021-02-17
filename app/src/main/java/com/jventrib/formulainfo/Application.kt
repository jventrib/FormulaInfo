package com.jventrib.formulainfo

import android.app.Application
import androidx.fragment.app.FragmentManager
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
//import leakcanary.LeakCanary
import timber.log.Timber

@ExperimentalCoroutinesApi
@FlowPreview
class Application: Application(), ImageLoaderFactory {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
//        LeakCanary.config = LeakCanary.config.copy(retainedVisibleThreshold = 1)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        appContainer = AppContainer(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .componentRegistry {
                add(SvgDecoder(applicationContext))
            }
            .crossfade(300)

            .build()
    }
}