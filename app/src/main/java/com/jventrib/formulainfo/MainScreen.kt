package com.jventrib.formulainfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.model.db.Session
import com.jventrib.formulainfo.ui.about.About
import com.jventrib.formulainfo.ui.common.composable.collectAsStateWithLifecycle
import com.jventrib.formulainfo.ui.laps.Laps
import com.jventrib.formulainfo.ui.laps.LapsViewModel
import com.jventrib.formulainfo.ui.preferences.PreferencesScreen
import com.jventrib.formulainfo.ui.race.LapChart
import com.jventrib.formulainfo.ui.race.RaceScreen
import com.jventrib.formulainfo.ui.race.RaceViewModel
import com.jventrib.formulainfo.ui.race.SessionState
import com.jventrib.formulainfo.ui.schedule.ScheduleScreen
import com.jventrib.formulainfo.ui.schedule.SeasonViewModel
import com.jventrib.formulainfo.ui.standing.DriverStandingChart
import com.jventrib.formulainfo.ui.standing.DriverStandingScreen
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme
import com.jventrib.formulainfo.utils.currentYear
import kotlinx.coroutines.launch

@ExperimentalCoilApi
@Composable
fun MainScreen() {
    FormulaInfoTheme {
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()

        NavHost(navController = navController, startDestination = "races") {
            composable("races") { navBackStackEntry ->
                val viewModel: SeasonViewModel = hiltViewModel(navBackStackEntry)

                val seasonList = viewModel.seasonList
                val selectedSeason by viewModel.season.collectAsStateWithLifecycle(currentYear())
                val raceList by viewModel.racesWithResults.collectAsStateWithLifecycle(listOf())

                ScheduleScreen(
                    raceList = raceList,
                    onRaceClicked = { race ->
                        //                        navController.navigate("resultsGraph/${race.raceInfo.season}/${race.raceInfo.round}")
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
                    onRefreshClicked = { scope.launch { viewModel.refresh() } },
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
            composable(
                "standing/{season}/{round}",
                listOf(
                    navArgument("season") { type = NavType.IntType },
                    navArgument("round") { type = NavType.IntType }
                )
            ) { navBackStackEntry ->
                val viewModel: RaceViewModel = hiltViewModel(navBackStackEntry)

                val season = navBackStackEntry.arguments?.get("season") as Int
                val round = (navBackStackEntry.arguments?.get("round") as Int)
                    .let { if (it == 0) null else it }

                LaunchedEffect(season, round) {
                    viewModel.setSeason(season)
                    viewModel.setRound(round)
                }

                val race by viewModel.race.collectAsState(null)
                val standings by viewModel.standings.collectAsState(null)
                standings?.let { st ->
                    DriverStandingScreen(
                        season = season,
                        race = race,
                        standings = st,
                        onDriverSelected = {}
                    ) {
                        navController.popBackStack()
                        navController.navigate("standing/$season/chart")
                    }
                }
            }
            composable(
                "standing/{season}/chart",
                listOf(navArgument("season") { type = NavType.IntType })
            ) { navBackStackEntry ->
                val viewModel: RaceViewModel = hiltViewModel(navBackStackEntry)

                val season = navBackStackEntry.arguments?.get("season") as Int

                LaunchedEffect(season) {
                    viewModel.setSeason(season)
                }

                val standings by viewModel.seasonStandingsChart.collectAsStateWithLifecycle(mapOf())
                DriverStandingChart(
                    season = season,
                    standings = standings,
                    onStandingClicked = {
                        navController.popBackStack()
                        navController.navigate("standing/$season/0")
                    }
                )
            }
            composable(
                "laps/{season}/{round}/{driver}",
                listOf(
                    navArgument("season") { type = NavType.IntType },
                    navArgument("round") { type = NavType.IntType },
                    navArgument("driver") { type = NavType.StringType }
                )
            ) { navBackStackEntry ->
                val viewModel: LapsViewModel = hiltViewModel(navBackStackEntry)

                val season = navBackStackEntry.arguments?.get("season") as Int
                val round = navBackStackEntry.arguments?.get("round") as Int
                val driverId = navBackStackEntry.arguments?.get("driver") as String

                LaunchedEffect(season, round, driverId) {
                    viewModel.setSeason(season)
                    viewModel.setRound(round)
                    viewModel.setDriverId(driverId)
                }

                val race by viewModel.race.collectAsStateWithLifecycle(null)
                val result by viewModel.result.collectAsStateWithLifecycle(null)
                val lapTimes by viewModel.laps.collectAsStateWithLifecycle(listOf())
                result?.let { Laps(race, it, lapTimes) }
            }
            composable(
                "resultsGraph/{season}/{round}",
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
                val resultsWithLaps by viewModel.resultsWithLaps.collectAsStateWithLifecycle(mapOf())

                if (resultsWithLaps.isNotEmpty()) {
                    LapChart(race, lapsByResult = resultsWithLaps)
                }
            }
            composable("preference") { PreferencesScreen() }
            composable("about") { About() }
        }
    }
}
