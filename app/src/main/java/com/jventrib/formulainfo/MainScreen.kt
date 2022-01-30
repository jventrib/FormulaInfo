package com.jventrib.formulainfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.ui.about.About
import com.jventrib.formulainfo.ui.laps.Laps
import com.jventrib.formulainfo.ui.laps.LapsViewModel
import com.jventrib.formulainfo.ui.results.LapChart
import com.jventrib.formulainfo.ui.results.ResultsScreen
import com.jventrib.formulainfo.ui.results.ResultsViewModel
import com.jventrib.formulainfo.ui.schedule.DriverStandingChart
import com.jventrib.formulainfo.ui.schedule.ScheduleScreen
import com.jventrib.formulainfo.ui.schedule.SeasonViewModel
import com.jventrib.formulainfo.ui.standing.DriverStandingScreen
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
                val raceList by viewModel.racesWithResults.observeAsState()
                val seasonList = viewModel.seasonList
                raceList?.let { rl ->
                    ScheduleScreen(
                        raceList = rl,
                        onRaceClicked = { race ->
                            //                        navController.navigate("resultsGraph/${race.raceInfo.season}/${race.raceInfo.round}")
                            navController.navigate("race/${race.raceInfo.season}/${race.raceInfo.round}")
                        },
                        seasonList = seasonList,
                        selectedSeason = viewModel.season.observeAsState().value,
                        onSeasonSelected = {
                            viewModel.season.value = it
                            viewModel.round.value = null
                        },
                        onAboutClicked = { navController.navigate("about") },
                        onRefreshClicked = { scope.launch { viewModel.refresh() } },
                        onStandingClicked = { navController.navigate("standing/${viewModel.season.value}/999") },
                        onStandingChartClicked = { navController.navigate("standing/${viewModel.season.value}/chart") }
                    )
                }
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
                        onDriverSelected = { driver ->
                            navController.navigate("laps/${season}/${round}/${driver.driverId}")
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
            composable(
                "standing/{season}/{round}",
                listOf(
                    navArgument("season") { type = NavType.IntType },
                    navArgument("round") { type = NavType.IntType })
            ) { navBackStackEntry ->
                val viewModel: ResultsViewModel = hiltViewModel(navBackStackEntry)
                val race by viewModel.race.observeAsState()
                val standings by viewModel.standings.observeAsState()
                val season = navBackStackEntry.arguments?.get("season") as Int
                val round = navBackStackEntry.arguments?.get("round") as Int
                viewModel.season.value = season
                viewModel.round.value = round
                standings?.let { st ->
                    DriverStandingScreen(
                        season = season,
                        race = race,
                        standings = st,
                        onDriverSelected = { driver ->
                            navController.navigate("laps/${season}/${round}/${driver.driverId}")
                        },
                        onRaceImageSelected = {},
                        onChartClicked = {
                            navController.navigate("resultsGraph/${it.raceInfo.season}/${it.raceInfo.round}")
                        },
                        onStandingChartClicked = {
                            navController.popBackStack()
                            navController.navigate("standing/${viewModel.season.value}/chart")
                        }
                    )
                }
            }
            composable(
                "standing/{season}/chart",
                listOf(navArgument("season") { type = NavType.IntType })
            ) { navBackStackEntry ->
                val viewModel: ResultsViewModel =
                    hiltViewModel(navController.currentBackStackEntry!!)
                val standings by viewModel.seasonStandings.observeAsState()

                val season = navBackStackEntry.arguments?.get("season") as Int
                viewModel.season.value = season
                viewModel.round.value = null

//                standings?.let {
                DriverStandingChart(
                    season = season,
                    standings = standings ?: mapOf(),
                    onStandingClicked = {
                        navController.popBackStack()
                        navController.navigate("standing/${viewModel.season.value}/999")
                    })
//                }
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
                val race by viewModel.race.observeAsState()

                result?.let { Laps(race, it, lapTimes?.dataOrNull() ?: listOf()) }
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
                val race by viewModel.race.observeAsState()

                val graph by viewModel.resultsWithLaps
                    .observeAsState(null)

                viewModel.season.value = season
                viewModel.round.value = round
                graph?.let {
                    if (it.isNotEmpty()) {
                        LapChart(race, lapsByResult = it)
                    }
                }
            }
            composable("about") { About() }
        }
    }
}

