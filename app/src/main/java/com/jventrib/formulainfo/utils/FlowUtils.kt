package com.jventrib.formulainfo.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onCompletion

fun <T> Flow<T>.concat(other: Flow<T>) = this.onCompletion { if (it == null) this.emitAll(other) }
