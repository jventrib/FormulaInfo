package com.jventrib.formulainfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComposeMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
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

