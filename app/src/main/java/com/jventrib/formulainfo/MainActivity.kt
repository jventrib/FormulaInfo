package com.jventrib.formulainfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.lifecycleScope
import coil.compose.LocalImageLoader
import com.jventrib.formulainfo.notification.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationViewModel: NotificationViewModel by viewModels()
        lifecycleScope.launch {
            notificationViewModel.notifyNextRace()
        }
        setContent {
            CompositionLocalProvider(LocalImageLoader provides viewModel.imageLoader) {
                MainScreen()
            }
        }
    }
}
