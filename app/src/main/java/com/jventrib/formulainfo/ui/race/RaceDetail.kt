package com.jventrib.formulainfo.ui.race

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.getRaceFullSample
import com.jventrib.formulainfo.model.db.FullRace
import com.jventrib.formulainfo.model.db.FullRaceResult
import com.jventrib.formulainfo.result.Results
import com.jventrib.formulainfo.ui.common.components.Image
import com.jventrib.formulainfo.ui.season.item.RaceItem
import logcat.LogPriority
import logcat.logcat
import kotlin.math.roundToInt

@ExperimentalCoilApi
@Composable
fun RaceDetail(fullRace: FullRace, raceResults: List<FullRaceResult>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Formula Info",
                        modifier = Modifier.clickable {})
                },
                actions = {
                }
            )
        }) {

        val circuitHeight = 192.dp
        val circuitHeightPx = with(LocalDensity.current) { circuitHeight.roundToPx().toFloat() }
        var circuitScrollHeightPx by remember { mutableStateOf(0f) }
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y
                    logcat(LogPriority.VERBOSE) { "delta $delta" }
                    circuitScrollHeightPx += delta
                    val height = circuitHeightPx + circuitScrollHeightPx
                    logcat(LogPriority.VERBOSE) { "height $height" }
                    return if (height > 0)
                        Offset.Infinite
                    else
                        Offset.Zero
                }
            }
        }
        Column(Modifier.nestedScroll(nestedScrollConnection)) {
            RaceItem(
                fullRace = fullRace,
            )
            Box(
                modifier = Modifier
                    .height(with(LocalDensity.current) {
                        (circuitHeightPx + circuitScrollHeightPx).coerceIn(0f, circuitHeightPx)
                            .roundToInt().toDp()
                    })
            ) {
                Image(
                    imageModel = fullRace.circuit.imageUrl,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            Results(results = raceResults)
        }
    }
}

@ExperimentalCoilApi
@Preview
@Composable
fun RaceDetailPreview() {
    RaceDetail(fullRace = getRaceFullSample(3), listOf())
}