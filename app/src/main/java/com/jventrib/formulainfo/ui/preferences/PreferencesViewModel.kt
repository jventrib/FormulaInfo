package com.jventrib.formulainfo.ui.preferences

import androidx.lifecycle.ViewModel
import com.jventrib.formulainfo.notification.SessionNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(val sessionNotificationManager: SessionNotificationManager) :
    ViewModel()
