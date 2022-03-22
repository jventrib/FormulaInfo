package com.jventrib.formulainfo.utils

import java.time.Instant
import java.time.Year

fun currentYear() = Year.now().value

fun now(): Instant = Instant.now()
