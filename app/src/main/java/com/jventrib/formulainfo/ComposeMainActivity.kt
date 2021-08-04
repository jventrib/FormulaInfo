package com.jventrib.formulainfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jventrib.formulainfo.race.ui.list.RaceScreen
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

