package com.jventrib.formulainfo.ui.race

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

        val circuitHeight = 246.dp
        val circuitHeightPx = with(LocalDensity.current) { circuitHeight.roundToPx().toFloat() }
        var circuitScrollHeightPx by remember { mutableStateOf(0f) }
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y
                    logcat(LogPriority.VERBOSE) { "delta $delta" }
                    circuitScrollHeightPx += delta
                    circuitScrollHeightPx = circuitScrollHeightPx.coerceIn(-circuitHeightPx, 0f)
                    return Offset.Zero
                }
            }
        }
        Box(Modifier.nestedScroll(nestedScrollConnection)) {
            Results(
                results = raceResults,
                contentPadding = PaddingValues(top = circuitHeight)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(circuitHeight)
//                    .padding(top = 80.dp)
                    .offset(0.dp, with(LocalDensity.current) {
                        (circuitScrollHeightPx)
//                            .coerceIn(-circuitHeightPx, 0f)
                            .roundToInt()
                            .toDp()
                    })
                    .background(Color.White)
//                    .border(1.dp, Color.Red),
            ) {
                Column {

                    RaceItem(
                        fullRace = fullRace,
                    )

                    Image(
                        imageModel = fullRace.circuit.imageUrl,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth().height(circuitHeight - 80.dp)
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