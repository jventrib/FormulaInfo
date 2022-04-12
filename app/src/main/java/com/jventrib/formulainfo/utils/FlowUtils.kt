package com.jventrib.formulainfo.utils

import java.time.Duration.between
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toKotlinDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
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
