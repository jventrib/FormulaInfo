package com.jventrib.formulainfo.ui.race

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MultilineChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.db.Session
import com.jventrib.formulainfo.ui.common.composable.Image
import com.jventrib.formulainfo.ui.common.composable.collectAsStateWithLifecycle
import com.jventrib.formulainfo.ui.schedule.getRaceSample
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme
import com.jventrib.formulainfo.utils.now
import kotlin.math.roundToInt

fun NavGraphBuilder.race(navController: NavHostController) {
    composable(
        "race/{season}/{round}",
        listOf(
            navArgument("season") { type = NavType.IntType },
            navArgument("round") { type = NavType.IntType }
        )
    ) { navBackStackEntry ->
        val viewModel: RaceViewModel = hiltViewModel(navBackStackEntry)

        val season = navBackStackEntry.arguments?.get("season") as Int
        val round = navBackStackEntry.arguments?.get("round") as Int

        LaunchedEffect(season, round) {
            viewModel.setSeason(season)
            viewModel.setRound(round)
        }

        val race by viewModel.race.collectAsStateWithLifecycle(null)
        val results by viewModel.results.collectAsStateWithLifecycle(listOf())
        val session = viewModel.session.collectAsStateWithLifecycle(Session.RACE)
        val sessionState = SessionState(results, session.value, viewModel::setSession)

        race?.let {
            RaceScreen(
                race = it,
                sessionState = sessionState,
                onDriverSelected = { driver ->
                    if (sessionState.session == Session.RACE) {
                        navController.navigate("laps/$season/$round/${driver.driverId}")
                    }
                },
                onRaceImageSelected = {},
                onChartClicked = {
                    navController.navigate("resultsGraph/${it.raceInfo.season}/${it.raceInfo.round}")
                },
                onStandingClicked = {
                    navController.navigate("standing/${it.raceInfo.season}/${it.raceInfo.round}")
                }
            )
        }
    }
}

@Composable
private fun RaceScreen(
    race: Race,
    sessionState: SessionState,
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
                    if (sessionState.results.isNotEmpty()) {
                        IconButton(
                            onClick = onStandingClicked,
                            modifier = Modifier.semantics { testTag = "standing" }
                        ) {
                            Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null)
                        }
                        if (sessionState.results.first().resultInfo.season > 1995) {
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
        val raceDetailHeight = if (race.raceInfo.sessions.fp1 != null) 147.dp else 80.dp
        val circuitHeight = 260.dp
        val tabHeight = 42.dp
        val headerHeight = raceDetailHeight + circuitHeight - 5.dp
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
        Box(
            Modifier
                .padding(it)
                .nestedScroll(nestedScrollConnection)
        ) {
            ResultsList(
                results = sessionState.results,
                contentPadding = PaddingValues(top = headerHeight + tabHeight),
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
                    RaceInfo(
                        race = race,
                        mode = if (now().isBefore(race.raceInfo.sessions.race)
                        ) RaceInfoMode.Maxi
                        else
                            RaceInfoMode.Expanded
                    )
                    Image(
                        imageModel = race.circuit.imageUrl,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .clickable { onRaceImageSelected(race) }
                            .fillMaxWidth()
                            .height(circuitHeight)
                            .background(Color.White)

                    )
                    val sessions = listOfNotNull(
                        Session.QUAL,
                        Session.SPRINT.takeIf { race.raceInfo.sessions.sprint != null },
                        Session.RACE
                    )
                    TabRow(
                        selectedTabIndex = sessions.indexOf(sessionState.session),
                    ) {
                        sessions.forEachIndexed { index, title ->
                            Tab(
                                text = { Text(title.label) },
                                selected = sessionState.session == sessions[index],
                                onClick = { sessionState.setSession(sessions[index]) }
                            )
                        }
                    }
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

@Preview
@Composable
fun RaceDetailPreview() {
    RaceScreen(
        race = getRaceSample(3),
        sessionState = remember {
            SessionState(listOf(getResultSample("verstappen", 1)), Session.RACE) {}
        },
        onDriverSelected = {},
        onRaceImageSelected = {},
        onChartClicked = {},
        onStandingClicked = {}
    )
}

@Preview
@Composable
fun RaceDetailDarkPreview() {
    FormulaInfoTheme(darkTheme = true) {
        RaceScreen(
            race = getRaceSample(3),
            sessionState = remember {
                SessionState(listOf(getResultSample("verstappen", 1)), Session.RACE) {}
            },
            onDriverSelected = {},
            onRaceImageSelected = {},
            onChartClicked = {},
            onStandingClicked = {}
        )
    }
}
