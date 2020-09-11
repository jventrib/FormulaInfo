package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.common.data.remote.BaseDataSource

class RaceRemoteDataSource(val raceService: RaceService) : BaseDataSource() {
    suspend fun getRaces() = getResult { raceService.getRaces() }
}
