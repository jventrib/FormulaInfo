package com.jventrib.formulainfo

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.map
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.annotation.ExperimentalCoilApi
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.ui.about.About
import com.jventrib.formulainfo.ui.laps.Laps
import com.jventrib.formulainfo.ui.laps.LapsViewModel
import com.jventrib.formulainfo.ui.results.ResultsScreen
import com.jventrib.formulainfo.ui.results.ResultsViewModel
import com.jventrib.formulainfo.ui.schedule.ScheduleScreen
import com.jventrib.formulainfo.ui.schedule.SeasonViewModel
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ExperimentalCoilApi
@Composable
fun MainScreen() {
    FormulaInfoTheme {
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()

        NavHost(navController = navController, startDestination = "races") {
            composable("races") {
                val viewModel: SeasonViewModel =
                    hiltViewModel(navController.currentBackStackEntry!!)
                val raceList by viewModel.races.observeAsState(
                    StoreResponse.Loading(ResponseOrigin.SourceOfTruth)
                )
                val seasonList = viewModel.seasonList
                ScheduleScreen(
                    raceList = raceList,
                    onRaceClicked = { race ->
                        navController.navigate("resultsGraph/${race.raceInfo.season}/${race.raceInfo.round}")
//                        navController.navigate("race/${race.raceInfo.season}/${race.raceInfo.round}")
                    },
                    seasonList = seasonList,
                    selectedSeason = viewModel.season.observeAsState().value,
                    onSeasonSelected = {
                        viewModel.season.value = it
                        viewModel.round.value = null
                    },
                    onAboutClicked = { navController.navigate("about") },
                    onRefreshClicked = { scope.launch { viewModel.refresh() } }
                )
            }
            composable(
                "race/{season}/{round}",
                listOf(
                    navArgument("season") { type = NavType.IntType },
                    navArgument("round") { type = NavType.IntType })
            ) { navBackStackEntry ->
                val viewModel: ResultsViewModel = hiltViewModel(navBackStackEntry)
                val race by viewModel.race.observeAsState()
                val results by viewModel.results.observeAsState()
                val season = navBackStackEntry.arguments?.get("season") as Int
                val round = navBackStackEntry.arguments?.get("round") as Int
                viewModel.season.value = season
                viewModel.round.value = round
                race?.let {
                    ResultsScreen(
                        race = it,
                        results = results?.dataOrNull() ?: listOf(),
                        onDriverSelected = { driver -> navController.navigate("laps/${season}/${round}/${driver.driverId}") }
                    )
                }
            }
            composable(
                "laps/{season}/{round}/{driver}",
                listOf(
                    navArgument("season") { type = NavType.IntType },
                    navArgument("round") { type = NavType.IntType },
                    navArgument("driver") { type = NavType.StringType })
            ) { navBackStackEntry ->
                val viewModel: LapsViewModel = hiltViewModel(navBackStackEntry)
                val result by viewModel.result.observeAsState()
                val lapTimes by viewModel.laps.observeAsState()

                viewModel.season.value = navBackStackEntry.arguments?.get("season") as Int
                viewModel.round.value = navBackStackEntry.arguments?.get("round") as Int
                viewModel.driverId.value = navBackStackEntry.arguments?.get("driver") as String
                result?.let { Laps(it, lapTimes?.dataOrNull() ?: listOf()) }
            }
            composable(
                "resultsGraph/{season}/{round}",
                listOf(
                    navArgument("season") { type = NavType.IntType },
                    navArgument("round") { type = NavType.IntType })
            ) { navBackStackEntry ->
                val viewModel: ResultsViewModel = hiltViewModel(navBackStackEntry)
                val season = navBackStackEntry.arguments?.get("season") as Int
                val round = navBackStackEntry.arguments?.get("round") as Int

//                val graph by viewModel.resultsGraph.observeAsState(null)
                val graph by viewModel.resultsGraph
                    .observeAsState(initial = mutableStateMapOf())

                viewModel.season.value = season
                viewModel.round.value = round
                graph?.let {
                    RaceGraphScreen(lapsByDriver = it)
                }
            }
            composable("about") { About() }
        }
    }
}

@Composable
fun RaceGraphScreen(lapsByDriver: Map<Driver, List<Lap>>) {
    LazyColumn {
        items(lapsByDriver.entries.sortedBy { it.value.maxByOrNull { it.number }?.position }.toList()) {
            Text(text = it.key.driverId + " -> " + it.value.size)
        }
    }
}

