package com.jventrib.formulainfo.ui.races

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.model.db.FullRace
import com.jventrib.formulainfo.ui.races.item.RaceItem

@Composable
fun Races(
    raceList: StoreResponse<List<FullRace>>,
    onRaceClicked: (FullRace) -> Unit,
    seasonList: List<Int>,
    selectedSeason: Int?,
    onSeasonSelected: (Int) -> Unit,
    onAboutClicked: () -> Unit,
    onRefreshClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Formula Info",
                        modifier = Modifier.clickable(onClick = onAboutClicked)
                    )
                },
                actions = {
                    IconButton(onClick = onRefreshClicked) {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                    }
                    SeasonMenu(
                        seasonList = seasonList,
                        selectedSeason = selectedSeason,
                        onSeasonSelect = onSeasonSelected
                    )
                }
            )
        }) {
        RaceList(raceList, onRaceClicked)
    }
}

@Composable
fun RaceList(raceList: StoreResponse<List<FullRace>>, onRaceSelected: (FullRace) -> Unit) {
    LazyColumn {
        raceList.dataOrNull()?.let { raceList ->
            items(raceList) {
                RaceItem(it, onRaceSelected = onRaceSelected)
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

