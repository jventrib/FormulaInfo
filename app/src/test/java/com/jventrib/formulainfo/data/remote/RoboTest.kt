package com.jventrib.formulainfo.data.remote

import com.jventrib.formulainfo.util.JULLogger
import dagger.hilt.android.testing.HiltAndroidRule
import java.util.logging.Level
import logcat.LogcatLogger
import org.junit.Before
import org.junit.Rule

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
