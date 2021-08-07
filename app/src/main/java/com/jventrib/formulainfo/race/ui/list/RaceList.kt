package com.jventrib.formulainfo.race.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.IMainViewModel
import com.jventrib.formulainfo.MockMainViewModel
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.ui.list.item.RaceItem
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme

@Composable
fun RaceScreen(viewModel: IMainViewModel, navController: NavHostController) {
    val raceList by viewModel.races.observeAsState(
        StoreResponse.Loading(ResponseOrigin.SourceOfTruth)
    )
    val seasonList = viewModel.seasonList
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Formula Info",
                        modifier = Modifier.clickable { navController.navigate("about") })
                },
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

@Composable
fun RaceList(raceList: StoreResponse<List<RaceFull>>) {
    LazyColumn {
        raceList.dataOrNull()?.let { raceList ->
            items(raceList) {
                RaceItem(it)
            }
        }
    }
}

//TODO look for a better way to provide a "mock" MainViewModel
//@Preview
//@Composable
//fun RaceScreenPreview() {
//    FormulaInfoTheme {
//        RaceScreen(viewModel = MockMainViewModel(), navController = NavController())
//    }
//}

