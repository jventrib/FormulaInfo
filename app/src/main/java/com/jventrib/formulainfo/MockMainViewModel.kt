package com.jventrib.formulainfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.model.db.RaceFull

class MockMainViewModel : IMainViewModel {
    override val seasonList: List<Int>
        get() = listOf(2021, 2020)

    override val races: LiveData<StoreResponse<List<RaceFull>>>
        get() {
            val list = listOf(
                getRaceFullSample(1),
                getRaceFullSample(2)
            )
            return MutableLiveData(StoreResponse.Data(list, ResponseOrigin.SourceOfTruth))
        }


    override val season: LiveData<Int>
        get() = MutableLiveData(2021)

    override fun setSeasonPosition(position: Int) {

    }
}
