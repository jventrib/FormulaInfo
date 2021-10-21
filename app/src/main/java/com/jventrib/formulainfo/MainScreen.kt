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
import com.jventrib.formulainfo.ui.race.RaceDetail
import com.jventrib.formulainfo.ui.race.RaceViewModel
import com.jventrib.formulainfo.ui.season.Season
import com.jventrib.formulainfo.ui.season.SeasonViewModel
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
                val viewModel: SeasonViewModel = hiltViewModel(navController.currentBackStackEntry!!)
                val raceList by viewModel.races.observeAsState(
                    StoreResponse.Loading(ResponseOrigin.SourceOfTruth)
                )
                val seasonList = viewModel.seasonList
                Season(
                    raceList = raceList,
                    onRaceClicked = { race ->
//                        navController.popBackStack()
//                        navController.popBackStack()
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
                val viewModel: RaceViewModel = hiltViewModel(navBackStackEntry)
                val fullRace by viewModel.fullRace.observeAsState()
                val raceResults by viewModel.raceResultsRaceResult.observeAsState()
                val season = navBackStackEntry.arguments?.get("season") as Int
                val round = navBackStackEntry.arguments?.get("round") as Int
                viewModel.season.value = season
                viewModel.round.value = round
                fullRace?.let {
                    RaceDetail(it, raceResults?.dataOrNull() ?: listOf())
                }
            }
            composable("about") { About() }
        }
    }
}

