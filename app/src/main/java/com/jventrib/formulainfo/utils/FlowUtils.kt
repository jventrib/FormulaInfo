package com.jventrib.formulainfo.utils

import java.lang.System.currentTimeMillis
import java.time.Duration.between
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toKotlinDuration
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.LogPriority
import logcat.logcat

fun <T> Flow<T>.concat(other: Flow<T>) = this.onCompletion { if (it == null) this.emitAll(other) }

fun Instant.countDownFlow(period: Duration): Flow<Duration> = flow {
    do {
        val between = between(now(), this@countDownFlow).toKotlinDuration()
        emit(between)
        logcat(LogPriority.VERBOSE) { "countDown: $between" }
        delay(period)
    } while (between.inWholeSeconds > 0)
}

fun <T> mutableSharedFlow() = MutableSharedFlow<T>(1)

fun <T> mutableSharedFlow(initial: T) = mutableSharedFlow<T>().apply { tryEmit(initial) }


suspend fun <T> throttled(
    mutex: Mutex,
    duration: Duration = 260.milliseconds,
    block: suspend () -> T
): T {
    mutex.withLock {
        val start = currentTimeMillis()
        val result = block()
        coroutineScope {
            launch {
                val executionDuration = currentTimeMillis() - start
                val wait = (duration.inWholeMilliseconds - executionDuration).coerceAtLeast(0)
                delay(wait)
            }
        }
        return result
    }
}
