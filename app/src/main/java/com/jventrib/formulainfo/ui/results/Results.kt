@file:JvmName("ResultsKt")

package com.jventrib.formulainfo.ui.results

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.FullRace
import com.jventrib.formulainfo.model.db.FullResult
import com.jventrib.formulainfo.result.DriverResult
import com.jventrib.formulainfo.ui.common.components.Image
import com.jventrib.formulainfo.ui.schedule.item.Race
import logcat.LogPriority
import logcat.logcat
import kotlin.math.roundToInt

@ExperimentalCoilApi
@Composable
fun ResultsScreen(fullRace: FullRace, results: List<FullResult>, onDriverSelected: (driver: Driver) -> Unit) {
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
            ResultsList(
                results = results,
                contentPadding = PaddingValues(top = headerHeight),
                onDriverSelected = onDriverSelected
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
                    Race(fullRace = fullRace, expanded = true)
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

@Composable
fun ResultsList(
    results: List<FullResult>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    onDriverSelected: (driver: Driver) -> Unit
) {
    LazyColumn(contentPadding = contentPadding, modifier = modifier) {
        items(results) { result ->
            DriverResult(result, onResultSelected = { onDriverSelected(it.driver) })
        }
    }

}

@ExperimentalCoilApi
@Preview
@Composable
fun RaceDetailPreview() {
    ResultsScreen(
        fullRace = getRaceFullSample(3),
        results = listOf(),
        onDriverSelected = {})
}