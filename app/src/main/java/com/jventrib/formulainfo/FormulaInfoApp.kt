package com.jventrib.formulainfo

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jventrib.formulainfo.about.About
import com.jventrib.formulainfo.race.ui.list.RaceScreen
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme

@Composable
fun FormulaInfoApp(viewModel: MainViewModel) {
    FormulaInfoTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "races") {
            composable("races") { RaceScreen(viewModel, navController) }
            composable("about") { About() }
        }
    }
}