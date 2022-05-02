package com.jventrib.formulainfo

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import com.jventrib.formulainfo.ui.about.about
import com.jventrib.formulainfo.ui.laps.laps
import com.jventrib.formulainfo.ui.preferences.preference
import com.jventrib.formulainfo.ui.race.lapChart
import com.jventrib.formulainfo.ui.race.race
import com.jventrib.formulainfo.ui.schedule.schedule
import com.jventrib.formulainfo.ui.standing.driverStanding
import com.jventrib.formulainfo.ui.standing.driverStandingChart
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme

@ExperimentalCoilApi
@Composable
fun MainScreen() {
    FormulaInfoTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "races") {
            schedule(navController)
            race(navController)
            driverStanding(navController)
            driverStandingChart(navController)
            laps()
            lapChart()
            preference()
            about()
        }
    }
}
