package com.jventrib.formulainfo

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.about.About
import com.jventrib.formulainfo.race.model.db.Circuit
import com.jventrib.formulainfo.race.model.db.Race
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.ui.detail.RaceDetail
import com.jventrib.formulainfo.race.ui.list.Races
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme
import java.time.Instant

@Composable
fun FormulaInfoApp(viewModel: MainViewModel) {
    FormulaInfoTheme {
        val navController = rememberNavController()
        val raceList by viewModel.races.observeAsState(
            StoreResponse.Loading(ResponseOrigin.SourceOfTruth)
        )
        val seasonList = viewModel.seasonList
        val raceFull by viewModel.raceFull.observeAsState()

        NavHost(navController = navController, startDestination = "races") {
            composable("races") {
                Races(
                    raceList = raceList,
                    onRaceClicked = { race -> navController.navigate("race/${race.race.season}/${race.race.round}") },
                    seasonList = seasonList,
                    selectedSeason = viewModel.season.observeAsState().value,
                    onSeasonSelected = { viewModel.season.value = it },
                    onAboutClicked = { navController.navigate("about") },
                )
            }
            composable(
                "race/{season}/{round}",
                listOf(
                    navArgument("season") { type = NavType.IntType },
                    navArgument("round") { type = NavType.IntType })
            ) { navBackStackEntry ->
                viewModel.season.value = navBackStackEntry.arguments?.get("season") as Int
                viewModel.round.value = navBackStackEntry.arguments?.get("round") as Int
                RaceDetail(raceFull!!)
            }
            composable("about") { About() }
        }
    }
}

