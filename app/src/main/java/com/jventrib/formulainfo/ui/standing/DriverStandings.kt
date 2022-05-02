package com.jventrib.formulainfo.ui.standing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MultilineChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.model.aggregate.DriverStanding
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.ui.race.RaceViewModel
import com.jventrib.formulainfo.ui.schedule.getRaceSample

@ExperimentalCoilApi
fun NavGraphBuilder.driverStanding(navController: NavHostController) {
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
}

@ExperimentalCoilApi
@Composable
private fun DriverStandingScreen(
    season: Int,
    race: Race?,
    standings: List<DriverStanding>,
    onDriverSelected: (driver: Driver) -> Unit,
    onStandingChartClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${race?.raceInfo?.raceName?.let { "$it " } ?: ""}$season standing",
                        modifier = Modifier.clickable {}
                    )
                },
                actions = {
                    IconButton(
                        onClick = onStandingChartClicked,
                        modifier = Modifier.semantics { testTag = "standingChart" }
                    ) {
                        Icon(imageVector = Icons.Filled.MultilineChart, contentDescription = null)
                    }
                }
            )
        }
    ) {
        LazyColumn {
            items(items = standings) { driverStanding ->
                DriverStanding(
                    driverStanding,
                    onDriverSelected = { onDriverSelected(driverStanding.driver) }
                )
            }
        }
    }
}

@ExperimentalCoilApi
@Preview
@Composable
fun DriverStandingScreenPreview() {
    DriverStandingScreen(
        season = 2021,
        race = getRaceSample(3),
        standings = listOf(),
        onDriverSelected = {},
    ) {}
}
