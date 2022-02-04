package com.jventrib.formulainfo.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MultilineChart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jventrib.formulainfo.model.aggregate.RaceWithResults
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.ui.schedule.item.Race
import kotlinx.coroutines.launch
import java.time.Year

@Composable
fun ScheduleScreen(
    raceList: List<RaceWithResults>,
    onRaceClicked: (Race) -> Unit,
    seasonList: List<Int>,
    selectedSeason: Int?,
    onSeasonSelected: (Int) -> Unit,
    onAboutClicked: () -> Unit,
    onRefreshClicked: () -> Unit,
    onStandingClicked: () -> Unit,
    onStandingChartClicked: () -> Unit
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
                    if (currentSeason(selectedSeason)) {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(
                                    index = raceList.indexOfFirst { it.race.nextRace }
                                )
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = null
                            )
                        }
                    }
                    if (raceList.any { it.results.isNotEmpty() }) {
                        IconButton(onClick = onStandingClicked) {
                            Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null)
                        }
                        IconButton(onClick = onStandingChartClicked) {
                            Icon(
                                imageVector = Icons.Filled.MultilineChart,
                                contentDescription = null
                            )
                        }
                    }
                    SeasonMenu(
                        seasonList = seasonList,
                        selectedSeason = selectedSeason,
                        onSeasonSelect = onSeasonSelected
                    )
                }
            )
        }) {
        RaceList(raceList, onRaceClicked, listState, onRefreshClicked)
    }
}

@Composable
fun RaceList(
    raceList: List<RaceWithResults>,
    onRaceSelected: (Race) -> Unit,
    listState: LazyListState,
    onRefresh: () -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(raceList.isEmpty()),
        onRefresh = onRefresh,
    ) {
        LazyColumn(state = listState) {
            items(raceList) {
                Race(
                    race = it.race,
                    results = it.results,
                    expanded = it.race.nextRace,
                    onRaceSelected = onRaceSelected
                )
            }
        }
    }
}

private fun currentSeason(selectedSeason: Int?) = Year.now().value == selectedSeason

//TODO look for a better way to provide a "mock" MainViewModel
//@Preview
//@Composable
//fun RaceScreenPreview() {
//    FormulaInfoTheme {
//        RaceScreen(viewModel = MockMainViewModel(), navController = NavController())
//    }
//}

