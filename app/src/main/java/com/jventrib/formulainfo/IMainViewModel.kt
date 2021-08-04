package com.jventrib.formulainfo

import androidx.lifecycle.LiveData
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.formulainfo.race.model.db.RaceFull

interface IMainViewModel {
    val seasonList: List<Int>
    val races: LiveData<StoreResponse<List<RaceFull>>>
    val season: LiveData<Int>
    fun setSeasonPosition(position: Int)
}