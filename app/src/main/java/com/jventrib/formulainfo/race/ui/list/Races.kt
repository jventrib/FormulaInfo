package com.jventrib.formulainfo.race.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.race.ui.list.item.RaceItem

@Composable
fun Races(
    raceList: StoreResponse<List<RaceFull>>,
    onRaceClicked: (RaceFull) -> Unit,
    seasonList: List<Int>,
    selectedSeason: Int?,
    onSeasonSelected: (Int) -> Unit,
    onAboutClicked: () -> Unit
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
fun RaceList(raceList: StoreResponse<List<RaceFull>>, onRaceSelected: (RaceFull) -> Unit) {
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
