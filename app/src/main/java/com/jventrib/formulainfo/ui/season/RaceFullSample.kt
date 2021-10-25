package com.jventrib.formulainfo

import com.jventrib.formulainfo.model.db.Circuit
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.FullRace
import java.time.Instant

fun getRaceFullSample(round: Int, name: String? = null): FullRace {
    val sessions = Race.Sessions(
        fp1 = Instant.now(),
        fp2 = Instant.now(),
        fp3 = Instant.now(),
        qualifying = Instant.now(),
        race = Instant.now()
    )
    val race = Race(2021, round, "", name ?: "Race$round", "Circuit$round", sessions)
    val circuit = Circuit(
        "Circuit1",
        "url",
        "Circuit one",
        Circuit.Location(
            1.0f,
            1.0f,
            "Fr",
            "France",
            "https://upload.wikimedia.org/wikipedia/en/thumb/9/9e/Flag_of_Japan.svg/100px-Flag_of_Japan.svg.png"
        ),
        "url"
    )

    return FullRace(race, circuit)
}