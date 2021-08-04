package com.jventrib.formulainfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.model.db.Circuit
import com.jventrib.formulainfo.race.model.db.Race
import com.jventrib.formulainfo.race.model.db.RaceFull
import java.time.Instant

class MockMainViewModel : IMainViewModel {
    override val seasonList: List<Int>
        get() = listOf(2021, 2020)

    override val races: LiveData<StoreResponse<List<RaceFull>>>
        get() {
            val sessions = Race.Sessions(race = Instant.now())
            val race = Race(2021, 1, "", "Race1", "Circuit1", sessions)
            val race2 = Race(2021, 2, "", "Race2", "Circuit1", sessions)
            val circuit = Circuit(
                "Circuit1",
                "url",
                "Circuit one",
                Circuit.Location(1.0f, 1.0f, "Fr", "France", "flag"),
                "url"
            )
            val list = listOf(
                RaceFull(race, circuit),
                RaceFull(race2, circuit)
            )
            return MutableLiveData(StoreResponse.Data(list, ResponseOrigin.SourceOfTruth))
        }
    override val season: LiveData<Int>
        get() = MutableLiveData(2021)

    override fun setSeasonPosition(position: Int) {

    }
}