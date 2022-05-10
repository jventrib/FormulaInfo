package com.jventrib.formulainfo.data.remote

import com.jventrib.formulainfo.util.JULLogger
import dagger.hilt.android.testing.HiltAndroidRule
import logcat.LogcatLogger
import org.junit.Before
import org.junit.Rule
import java.util.logging.Level

open class RoboTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
        JULLogger.level = Level.FINEST
        if (!LogcatLogger.isInstalled) LogcatLogger.install(JULLogger)
    }
}
