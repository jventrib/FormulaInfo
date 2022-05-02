package com.jventrib.formulainfo.ui.standing

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jventrib.formulainfo.model.aggregate.DriverAndConstructor
import com.jventrib.formulainfo.model.aggregate.DriverStanding
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.ui.common.composable.Chart
import com.jventrib.formulainfo.ui.common.composable.DataPoint
import com.jventrib.formulainfo.ui.common.composable.Serie
import com.jventrib.formulainfo.ui.common.composable.YOrientation
import com.jventrib.formulainfo.ui.common.composable.collectAsStateWithLifecycle
import com.jventrib.formulainfo.ui.drivers.DriverSelector
import com.jventrib.formulainfo.ui.drivers.customShape
import com.jventrib.formulainfo.ui.drivers.driverSelectionSaver
import com.jventrib.formulainfo.ui.race.RaceViewModel
import com.jventrib.formulainfo.ui.theme.color
import logcat.logcat

fun NavGraphBuilder.driverStandingChart(navController: NavHostController) {
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

        if (standings.isNotEmpty()) {
            DriverStandingChart(
                season = season,
                standings = standings,
                onStandingClicked = {
                    navController.popBackStack()
                    navController.navigate("standing/$season/0")
                }
            )
        }
    }
}

@Composable
private fun DriverStandingChart(
    season: Int,
    standings: Map<Driver, List<DriverStanding>>,
    onStandingClicked: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    val selectedDrivers =
        rememberSaveable(standings, key = "standingDrivers", saver = driverSelectionSaver) {
            logcat("Standing") { "Init standings: ${standings.count()}" }
            standings.keys.map { it.driverId to true }.toMutableStateMap()
        }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "$season standing",
                        modifier = Modifier.clickable {}
                    )
                },
                actions = {
                    IconButton(
                        onClick = onStandingClicked,
                        modifier = Modifier.semantics { testTag = "standing" }
                    ) {
                        Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null)
                    }
                }
            )
        },
        drawerShape = customShape(),
        drawerContent = {
            DriverSelector(
                drivers = standings.values.map { it.last() }.sortedByDescending { it.points }
                    .map { DriverAndConstructor(it.driver, it.constructor) },
                selectedDrivers
            )
        }

    ) {

        val series = standings
            .filter {
                selectedDrivers[it.key.driverId] ?: false
            }
            .map { entry ->
                Serie(
                    entry.value.map { round ->
                        DataPoint(
                            round,
                            Offset(
                                round.round!!.toFloat(),
                                round.points
                            )
                        )
                    },
                    entry.value.first().constructor.color,
                    entry.key.code ?: entry.key.driverId.take(3)
                )
            }

        Chart(
            series = series, yOrientation = YOrientation.Up, gridStep = Offset(5f, 10f),
        )
    }
}
