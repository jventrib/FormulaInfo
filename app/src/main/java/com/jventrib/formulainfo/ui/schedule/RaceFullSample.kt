package com.jventrib.formulainfo.ui.schedule

import com.jventrib.formulainfo.model.db.Circuit
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.RaceInfo
import com.jventrib.formulainfo.utils.now
import java.time.temporal.ChronoUnit.DAYS

fun getRaceSample(round: Int, name: String? = null): Race {
    val sessions = RaceInfo.Sessions(
        fp1 = now(),
        fp2 = now(),
        fp3 = now(),
        qualifying = now(),
        race = now().plus(7, DAYS).plusSeconds(10000)
    )
    val race = RaceInfo(2021, round, "", name ?: "Race$round", "Circuit$round", sessions)
    val circuit = Circuit(
        "Circuit1",
        "url",
        "Circuit one",
        Circuit.Location(
            1.0f,
            1.0f,
            "Fr",
            "France",
            "https://upload.wikimedia.org/wikipedia/en/thumb" +
                "/9/9e/Flag_of_Japan.svg/100px-Flag_of_Japan.svg.png"
        ),
        "url"
    )

    return Race(race, circuit)
}
