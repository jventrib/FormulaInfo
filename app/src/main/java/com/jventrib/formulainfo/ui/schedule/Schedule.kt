package com.jventrib.formulainfo.ui.schedule

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MultilineChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jventrib.formulainfo.model.aggregate.RaceWithResults
import com.jventrib.formulainfo.model.db.Race
import kotlinx.coroutines.delay

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
    var alreadyScrolled by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(alreadyScrolled) {
        if (raceList.any { it.race.nextRace } && !alreadyScrolled) {
            delay(300)
            listState.animateScrollToItem(index = raceList.indexOfFirst { it.race.nextRace })
            alreadyScrolled = true
            Toast.makeText(context, "Auto scrolled to next race", Toast.LENGTH_SHORT).show()
        }
    }
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
                    if (raceList.any { it.results.isNotEmpty() }) {
                        IconButton(
                            onClick = onStandingClicked,
                            modifier = Modifier.semantics { testTag = "standing" }
                        ) {
                            Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null)
                        }
                        IconButton(
                            onClick = onStandingChartClicked,
                            modifier = Modifier.semantics { testTag = "standingChart" }
                        ) {
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
        }
    ) {
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
        modifier = Modifier.semantics { testTag = "raceListSwipe" }
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

// TODO look for a better way to provide a "mock" MainViewModel
// @Preview
// @Composable
// fun RaceScreenPreview() {
//    FormulaInfoTheme {
//        RaceScreen(viewModel = MockMainViewModel(), navController = NavController())
//    }
// }
