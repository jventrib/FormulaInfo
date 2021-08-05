package com.jventrib.formulainfo.race.ui.list

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.IMainViewModel
import com.jventrib.formulainfo.MockMainViewModel
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme

@Composable
fun RaceScreen(viewModel: IMainViewModel) {
    val raceList by viewModel.races.observeAsState(
        StoreResponse.Loading(ResponseOrigin.SourceOfTruth)
    )
    val seasonList = viewModel.seasonList
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Formula Info") },
                actions = {
                    SeasonMenu(
                        seasonList,
                        viewModel.season.observeAsState().value
                    ) { viewModel.setSeasonPosition(it) }
                }
            )
        }) {
        RaceList(raceList)
    }
}

//TODO look for a better way to provide a "mock" MainViewModel
@Preview
@Composable
fun RaceScreenPreview() {
    FormulaInfoTheme {
        RaceScreen(viewModel = MockMainViewModel())
    }
}

