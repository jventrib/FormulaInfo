package com.jventrib.formulainfo.ui.race

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
        val raceDetailHeight = 140.dp
        val circuitHeight = 150.dp
        val headerHeight = raceDetailHeight + circuitHeight
        var circuitScrollHeightPx by remember { mutableStateOf(0f) }
        val headerHeightPx = with(LocalDensity.current) { headerHeight.roundToPx().toFloat() }
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y
                    logcat(LogPriority.VERBOSE) { "delta $delta" }
                    circuitScrollHeightPx =
                        (circuitScrollHeightPx + delta).coerceIn(-headerHeightPx, 0f)
                    return Offset.Zero
                }
            }
        }
        Box(Modifier.nestedScroll(nestedScrollConnection)) {
            Results(
                results = raceResults,
                contentPadding = PaddingValues(top = headerHeight)
            )
            Box(
                modifier = Modifier
                    .offset(0.dp, with(LocalDensity.current) {
                        (circuitScrollHeightPx)
                            .roundToInt()
                            .toDp()
                    })
                    .background(Color.White)
            ) {
                Column {
                    RaceItem(fullRace = fullRace, expanded = true)
                    Image(
                        imageModel = fullRace.circuit.imageUrl,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(circuitHeight)
                    )
                }
            }
        }
    }
}

@ExperimentalCoilApi
@Preview
@Composable
fun RaceDetailPreview() {
    RaceDetail(fullRace = getRaceFullSample(3), listOf())
}