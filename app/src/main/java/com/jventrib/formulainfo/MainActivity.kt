package com.jventrib.formulainfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.lifecycleScope
import coil.compose.LocalImageLoader
import com.jventrib.formulainfo.notification.SessionNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionNotificationManager: SessionNotificationManager

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            sessionNotificationManager.notifyNextRaces()
        }
        setContent {
            CompositionLocalProvider(LocalImageLoader provides viewModel.imageLoader) {
                MainScreen()
            }
        }
    }
}
