package com.jventrib.formulainfo.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.ui.schedule.item.Race
import kotlinx.coroutines.launch

@Composable
fun ScheduleScreen(
    raceList: StoreResponse<List<Race>>,
    onRaceClicked: (Race) -> Unit,
    seasonList: List<Int>,
    selectedSeason: Int?,
    onSeasonSelected: (Int) -> Unit,
    onAboutClicked: () -> Unit,
    onRefreshClicked: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

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
                    IconButton(onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(
                                index = raceList.dataOrNull()?.indexOfFirst { it.nextRace } ?: 0
                            )
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.Notifications, contentDescription = null)
                    }
                    SeasonMenu(
                        seasonList = seasonList,
                        selectedSeason = selectedSeason,
                        onSeasonSelect = onSeasonSelected
                    )
                }
            )
        }) {
        RaceList(raceList, onRaceClicked, listState)
    }
}

@Composable
fun RaceList(
    raceList: StoreResponse<List<Race>>,
    onRaceSelected: (Race) -> Unit,
    listState: LazyListState
) {
    LazyColumn(state = listState) {
        raceList.dataOrNull()?.let { raceList ->
            items(raceList) {
                Race(
                    race = it,
                    expanded = it.nextRace,
                    onRaceSelected = onRaceSelected
                )
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

