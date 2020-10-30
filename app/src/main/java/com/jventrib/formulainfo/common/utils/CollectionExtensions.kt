package com.jventrib.formulainfo.common.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T> List<T>.emptyFlowOfListToNull() = if (this.isEmpty()) null else this

fun <T> Flow<List<T>>.emptyFlowOfListToNull() = this.map { it.emptyFlowOfListToNull() }