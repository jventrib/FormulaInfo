package com.jventrib.formulainfo.ui.results

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MultilineChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.ui.common.composable.Image
import com.jventrib.formulainfo.ui.schedule.Race
import com.jventrib.formulainfo.ui.schedule.getRaceSample
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme
import kotlin.math.roundToInt

@ExperimentalCoilApi
@Composable
fun ResultsScreen(
    race: Race,
    results: List<Result>,
    onDriverSelected: (driver: Driver) -> Unit,
    onRaceImageSelected: (Race) -> Unit,
    onChartClicked: () -> Unit,
    onStandingClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${race.raceInfo.season} season",
                        modifier = Modifier.clickable {}
                    )
                },
                actions = {
                    if (results.isNotEmpty()) {
                        IconButton(
                            onClick = onStandingClicked,
                            modifier = Modifier.semantics { testTag = "standing" }
                        ) {
                            Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null)
                        }
                        if (results.first().resultInfo.season > 1995) {
                            IconButton(
                                onClick = onChartClicked,
                                modifier = Modifier.semantics { testTag = "resultChart" }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MultilineChart,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            )
        }
    ) {
        val raceDetailHeight = if (race.raceInfo.sessions.fp1 != null) 140.dp else 80.dp
        val circuitHeight = 150.dp
        val headerHeight = raceDetailHeight + circuitHeight
        var circuitScrollHeightPx by remember { mutableStateOf(0f) }
        val headerHeightPx = with(LocalDensity.current) { headerHeight.roundToPx().toFloat() }
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y
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
                    .offset(
                        0.dp,
                        with(LocalDensity.current) {
                            (circuitScrollHeightPx)
                                .roundToInt()
                                .toDp()
                        }
                    )
                    .background(MaterialTheme.colors.background)
            ) {
                Column {
                    Race(race = race, expanded = true)
                    Image(
                        imageModel = race.circuit.imageUrl,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .clickable { onRaceImageSelected(race) }
                            .fillMaxWidth()
                            .height(circuitHeight)
                            .background(Color.White)

                    )
                }
            }
        }
    }
}

@Composable
fun ResultsList(
    results: List<Result>,
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
        race = getRaceSample(3),
        results = listOf(),
        onDriverSelected = {},
        onRaceImageSelected = {},
        onChartClicked = {},
        onStandingClicked = {}
    )
}

@ExperimentalCoilApi
@Preview
@Composable
fun RaceDetailDarkPreview() {
    FormulaInfoTheme(darkTheme = true) {
        ResultsScreen(
            race = getRaceSample(3),
            results = listOf(),
            onDriverSelected = {},
            onRaceImageSelected = {},
            onChartClicked = {},
            onStandingClicked = {}
        )
    }
}
