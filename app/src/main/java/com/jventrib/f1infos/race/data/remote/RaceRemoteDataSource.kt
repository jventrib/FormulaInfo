package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.race.data.RaceRepository
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.model.RaceResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.URLDecoder
import java.util.*

open class RaceRemoteDataSource(
    private val mrdService: RaceService,
    private val countryService: CountryService,
    private val wikipediaService: WikipediaService,
    private val f1calendarService: F1CalendarService,

    ) {

    fun getRacesFlow(season: Int): Flow<List<Race>> = flow {
        val races = getRaces(season)
        //First emit with all races, no flag loaded
        emit(races)

        //Then load the flags
        races.forEach {
            it.circuit.location.flag =
                getCountryFlag(it.circuit.location.country)
            //Each time a flag is load, emit all the races
            it.circuit.circuitImageUrl = getCircuitImage(it.circuit.circuitUrl)
            emit(races)
        }
    }

    suspend fun getRaces(season: Int): List<Race> = mrdService.getRaces(season).mrData.table.races
        .zip(f1calendarService.getRaces(season).races) { mrd, f1c ->
            mrd.sessions = f1c.sessions
            mrd
        }

    suspend fun getCountryFlag(country: String) =
        countryService.getCountry(country).last().alpha2Code.toLowerCase(Locale.ROOT)

    private suspend fun getCircuitImage(circuitUrl: String): String {
        val name = URLDecoder.decode(circuitUrl.splitToSequence("/").last(), Charsets.UTF_8.name())
        val pageImage = wikipediaService.getPageImage(name)
        val query = pageImage.query
        return query.pages.values.first().original.source
    }

    fun getRaceResultsFlow(season: Int, round: Int): Flow<List<RaceResult>> {
        return flow{emit(mrdService.getRaceResults(season, round).mrData.table.races.first().results!!)}
    }

}
