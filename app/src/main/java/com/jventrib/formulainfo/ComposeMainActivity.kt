package com.jventrib.formulainfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.model.db.RaceFull
import com.jventrib.formulainfo.ui.theme.FormulaInfoTheme

class ComposeMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val raceRepository = (application as Application).appContainer.raceRepository
        val viewModel = MainViewModel(raceRepository)
        super.onCreate(savedInstanceState)
        viewModel.setSeasonPosition(0)
        setContent {
            FormulaInfoTheme {
                RaceScreen(viewModel)
            }
        }
    }

}

@Composable
private fun RaceScreen(viewModel: IMainViewModel) {
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

@Composable
fun RaceList(raceList: StoreResponse<List<RaceFull>>) {
    Column {
        raceList.dataOrNull()?.forEach {
            Text(text = it.race.raceName)
        }
    }
}


@Composable
fun SeasonMenu(
    seasonList: List<Int>,
    selectedSeason: Int?,
    initiallyExpanded: Boolean = false,
    onSeasonSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { expanded = !expanded }) {
        Text(selectedSeason.toString())
        Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        seasonList.forEachIndexed { index, season ->
            DropdownMenuItem(onClick = {
                expanded = false
                onSeasonSelect(index)
            }, modifier = Modifier.sizeIn(maxWidth = 70.dp)) {
                Text(season.toString())
            }
        }
    }
}


//TODO look for a better way to provide a "mock" MainViewModel
@Preview
@Composable
fun RaceScreenPreview() {
    val vm = MockMainViewModel()
    RaceScreen(viewModel = vm)
}

@Preview
@Composable
fun SeasonMenuPreview() {
    SeasonMenu(listOf(2021, 2020, 2019), 2021, true) {}
//    DropdownMenuItem(
//        onClick = { /*TODO*/ }, modifier = Modifier.sizeIn(
//            maxWidth = 80.dp
//        )
//    ) {
//        Text(text = "Test")
//    }
}

//@Preview
//@Composable
//fun RaceListPreview() {
//    val sessions = Race.Sessions(race = Instant.now())
//    val race = Race(2021, 1, "", "Race1", "Circuit1", sessions)
//    val race2 = Race(2021, 2, "", "Race2", "Circuit1", sessions)
//    val circuit = Circuit(
//        "Circuit1",
//        "url",
//        "Circuit one",
//        Circuit.Location(1.0f, 1.0f, "Fr", "France", "flag"),
//        "url"
//    )
//    val list = listOf(
//        RaceFull(race, circuit),
//        RaceFull(race2, circuit)
//    )
//    RaceList(
//        raceList = StoreResponse.Data(
//            list, ResponseOrigin.SourceOfTruth
//        )
//    )
//}

