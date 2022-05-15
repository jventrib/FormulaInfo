package com.jventrib.formulainfo.util

import logcat.LogPriority
import logcat.LogcatLogger
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

object JULLogger : LogcatLogger {
    private var handler = ConsoleHandler().also {
        it.formatter = SimpleFormatter()
    }
    var level: Level = Level.FINE
        set(value) {
            handler = ConsoleHandler().also {
                it.formatter = SimpleFormatter()
                it.level = value
            }
            field = value
        }

    init {
        System.setProperty(
            "java.util.logging.SimpleFormatter.format", "[%1\$tF %1\$tT] [%4$-4s] %5\$s %n"
        )
    }

    private val levelMapping = mapOf(
        LogPriority.VERBOSE to Level.FINEST,
        LogPriority.DEBUG to Level.FINE,
        LogPriority.INFO to Level.INFO,
        LogPriority.WARN to Level.WARNING,
        LogPriority.ERROR to Level.SEVERE,
        LogPriority.ASSERT to Level.SEVERE,

    )

    override fun log(priority: LogPriority, tag: String, message: String) {
        Logger.getLogger(tag)
            .also {
                it.level = level
                it.addHandler(handler)
            }
            .log(levelMapping.getValue(priority), message)
    }
}
