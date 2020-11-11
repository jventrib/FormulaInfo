package com.jventrib.formulainfo.race.data.remote

import com.jventrib.formulainfo.race.model.remote.RaceRemote
import com.jventrib.formulainfo.race.model.remote.RaceResultRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.URLDecoder
import java.time.ZonedDateTime

const val DEFAULT_IMAGE_SIZE = 100

open class RaceRemoteDataSource(
    private val mrdService: MrdService,
    private val wikipediaService: WikipediaService,
    private val f1calendarService: F1CalendarService,

    ) {

    fun getRacesFlow(season: Int) = flow { emit(getRaces(season)) }

    private suspend fun getRaces(season: Int): List<RaceRemote> {

        val races = mrdService.getRaces(season).mrData.table.races
        return try {
            races.zip(f1calendarService.getRaces(season).races) { mrd, f1c ->
                mrd.sessions = f1c.sessions
                mrd
            }
        } catch (e: Exception) {
            races.onEach {
                val raceDateTime = if (it.timeInitialized) {
                    ZonedDateTime.parse("${it.date}T${it.time}").toInstant()
                } else {
                    ZonedDateTime.parse("${it.date}T15:00:00Z").toInstant()
                }
                it.sessions = RaceRemote.Sessions(race = raceDateTime)
            }
        }
    }

    fun getRaceResultsFlow(season: Int, round: Int): Flow<List<RaceResultRemote>> {
        return flow {
            val raceResults =
                mrdService.getRaceResults(season, round)
                    .mrData.table.races.firstOrNull()?.resultRemotes
                    ?.map { it.copy(season = season, round = round) }
            raceResults?.let { emit(it) } ?: emit(listOf<RaceResultRemote>())
        }
    }

    suspend fun getCountryFlag(country: String) =
        getWikipediaImage(country, DEFAULT_IMAGE_SIZE, WikipediaService.Licence.FREE)

    suspend fun getCircuitImage(circuitUrl: String, size: Int = DEFAULT_IMAGE_SIZE) =
        getWikipediaImage(getWikipediaTitle(circuitUrl), size, WikipediaService.Licence.FREE)

    suspend fun getWikipediaImageFromUrl(
        s: String,
        size: Int = DEFAULT_IMAGE_SIZE,
        license: WikipediaService.Licence = WikipediaService.Licence.ANY
    ) =
        getWikipediaImage(getWikipediaTitle(s), size, license)

    private suspend fun getWikipediaImage(
        name: String,
        size: Int = DEFAULT_IMAGE_SIZE,
        license: WikipediaService.Licence = WikipediaService.Licence.ANY
    ) =
        wikipediaService.getPageImage(name, size, license.param).query?.pages?.values?.first()
            ?.let { it.original?.source ?: it.thumbnail?.source }

    private fun getWikipediaTitle(circuitUrl: String): String {
        return URLDecoder.decode(
            circuitUrl.splitToSequence("/").last(),
            Charsets.UTF_8.name()
        )
    }

}
