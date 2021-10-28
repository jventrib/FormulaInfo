package com.jventrib.formulainfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.annotation.ExperimentalCoilApi
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.ui.about.About
import com.jventrib.formulainfo.ui.lap.LapViewModel
import com.jventrib.formulainfo.ui.lap.LapsDetail
import com.jventrib.formulainfo.ui.results.ResultsScreen
import com.jventrib.formulainfo.ui.results.ResultsViewModel
import com.jventrib.formulainfo.ui.schedule.ScheduleScreen
import com.jventrib.formulainfo.ui.schedule.SeasonViewModel
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme
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
                        navController.navigate("race/${race.race.season}/${race.race.round}")
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
                val fullRace by viewModel.fullRace.observeAsState()
                val results by viewModel.results.observeAsState()
                val season = navBackStackEntry.arguments?.get("season") as Int
                val round = navBackStackEntry.arguments?.get("round") as Int
                viewModel.season.value = season
                viewModel.round.value = round
                fullRace?.let {
                    ResultsScreen(
                        fullRace = it,
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
                val viewModel: LapViewModel = hiltViewModel(navBackStackEntry)
                val result by viewModel.result.observeAsState()
                val lapTimes by viewModel.laps.observeAsState()

                viewModel.season.value = navBackStackEntry.arguments?.get("season") as Int
                viewModel.round.value = navBackStackEntry.arguments?.get("round") as Int
                viewModel.driverId.value = navBackStackEntry.arguments?.get("driver") as String
                result?.let { LapsDetail(it, lapTimes?.dataOrNull() ?: listOf()) }
            }
            composable("about") { About() }
        }
    }
}

