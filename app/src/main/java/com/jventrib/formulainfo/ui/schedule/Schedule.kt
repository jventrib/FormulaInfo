@file:OptIn(ExperimentalMaterialApi::class)

package com.jventrib.formulainfo.ui.schedule

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MultilineChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jventrib.formulainfo.model.aggregate.RaceWithResults
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.ui.common.composable.collectAsStateWithLifecycle
import com.jventrib.formulainfo.ui.race.RaceInfo
import com.jventrib.formulainfo.ui.race.RaceInfoMode
import com.jventrib.formulainfo.utils.currentYear
import kotlinx.coroutines.delay
import logcat.logcat

fun NavGraphBuilder.schedule(navController: NavHostController) {
    composable("races") {
        val viewModel: SeasonViewModel = hiltViewModel()

        val seasonList = viewModel.seasonList
        val selectedSeason by viewModel.season.collectAsStateWithLifecycle(currentYear())
        val raceList by viewModel.racesWithResults.collectAsStateWithLifecycle(listOf())

        ScheduleScreen(
            raceList = raceList,
            onRaceClicked = { race ->
                navController.navigate(
                    "race/${race.raceInfo.season}/${race.raceInfo.round}"
                )
            },
            seasonList = seasonList,
            selectedSeason = selectedSeason,
            onSeasonSelected = {
                viewModel.setSeason(it)
            },
            onAboutClicked = { navController.navigate("about") },
            onPreferenceClicked = { navController.navigate("preference") },
            onRefreshClicked = { viewModel.refresh() },
            onStandingClicked = {
                navController.navigate(
                    "standing/$selectedSeason/0"
                )
            },
            onStandingChartClicked = {
                navController.navigate(
                    "standing/$selectedSeason/chart"
                )
            }
        )
    }
}

@Composable
private fun ScheduleScreen(
    raceList: List<RaceWithResults>,
    onRaceClicked: (Race) -> Unit,
    seasonList: List<Int>,
    selectedSeason: Int?,
    onSeasonSelected: (Int) -> Unit,
    onAboutClicked: () -> Unit,
    onPreferenceClicked: () -> Unit,
    onRefreshClicked: () -> Unit,
    onStandingClicked: () -> Unit,
    onStandingChartClicked: () -> Unit
) {
    val listState = rememberLazyListState()
    var alreadyScrolled by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(raceList, alreadyScrolled) {
        logcat { "autoscroll: " + (raceList.any { it.race.nextRace }) }
        if (raceList.any { it.race.nextRace } && !alreadyScrolled) {
            delay(500)
            listState.animateScrollToItem(index = raceList.indexOfFirst { it.race.nextRace })
            alreadyScrolled = true
            Toast.makeText(
                context,
                """Auto scrolled to next race
                |Scroll up  for previous races
                """.trimMargin(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                "Formula Info",
                modifier = Modifier
                    .clickable(onClick = onAboutClicked)
                    .semantics { testTag = "about" }
            )
        }, actions = {
                if (raceList.any { it.results.isNotEmpty() }) {
                    IconButton(
                        onClick = onStandingClicked,
                        modifier = Modifier.semantics { testTag = "standing" }
                    ) {
                        Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null)
                    }
                    IconButton(
                        onClick = onStandingChartClicked,
                        modifier = Modifier.semantics { testTag = "standingChart" }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MultilineChart, contentDescription = null
                        )
                    }
                }
                IconButton(
                    onClick = onPreferenceClicked,
                    modifier = Modifier.semantics { testTag = "preference" }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings, contentDescription = null
                    )
                }
                SeasonMenu(
                    seasonList = seasonList,
                    selectedSeason = selectedSeason,
                    onSeasonSelect = onSeasonSelected
                )
            })
    }) {
        Box(modifier = Modifier.padding(it)) {
            RaceList(raceList, onRaceClicked, listState, onRefreshClicked)
        }
    }
}

@Composable
fun RaceList(
    raceList: List<RaceWithResults>,
    onRaceSelected: (Race) -> Unit,
    listState: LazyListState,
    onRefresh: () -> Unit
) {

    val pullRefreshState = rememberPullRefreshState(raceList.isEmpty(), onRefresh)

    Box(Modifier.pullRefresh(pullRefreshState).then(Modifier.semantics { testTag = "raceListSwipe" }
    )) {
        LazyColumn(state = listState, modifier = Modifier.semantics { testTag = "raceList" }) {
            items(raceList) {
                RaceInfo(
                    race = it.race,
                    results = it.results,
                    mode = if (it.race.nextRace) RaceInfoMode.Maxi else RaceInfoMode.Mini,
                    onRaceSelected = onRaceSelected
                )
            }
        }
    }
}

// TODO look for a better way to provide a "mock" MainViewModel
// @Preview
// @Composable
// fun RaceScreenPreview() {
//    FormulaInfoTheme {
//        RaceScreen(viewModel = MockMainViewModel(), navController = NavController())
//    }
// }
