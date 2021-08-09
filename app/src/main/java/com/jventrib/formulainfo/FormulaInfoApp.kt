package com.jventrib.formulainfo

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.jventrib.formulainfo.about.About
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.ui.detail.RaceDetail
import com.jventrib.formulainfo.race.ui.list.Races
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme

@Composable
fun FormulaInfoApp(viewModel: MainViewModel) {
    FormulaInfoTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "races") {
            composable("races") { Races(viewModel, navController) }
            composable(
                "raceDetail/{raceId}",
                listOf(navArgument("raceId") {
                    type = NavType.IntType
                })
            ) { navBackStackEntry ->
                RaceDetail(
                    viewModel,
                    navController,
                    navBackStackEntry.arguments?.get("race") as RaceFull
                )
            }
            composable("about") { About() }
        }
    }
}

