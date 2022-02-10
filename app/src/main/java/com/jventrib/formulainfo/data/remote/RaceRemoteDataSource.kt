package com.jventrib.formulainfo.data.remote

import com.jventrib.formulainfo.model.remote.RaceRemote
import com.jventrib.formulainfo.model.remote.ResultRemote
import java.net.URLDecoder
import java.time.Instant
import java.time.Year
import java.time.ZonedDateTime

const val DEFAULT_IMAGE_SIZE = 100

open class RaceRemoteDataSource(
    private val mrdService: MrdService,
    private val wikipediaService: WikipediaService,
    private val f1calendarService: F1CalendarService
) {

    suspend fun getRaces(season: Int): List<RaceRemote> {

        val races = mrdService.getSchedule(season).mrData.table.races
        return if (season >= Year.now().value) {
            try {
                zipMrdAndF1cSessions(races, season)
            } catch (e: Exception) {
                getRacesRaceTimes(races)
            }
        } else {
            getRacesRaceTimes(races)
        }
    }

    private fun getRacesRaceTimes(races: List<RaceRemote>) =
        races.onEach {
            val raceDateTime = it.getRaceTime()
            it.sessions = RaceRemote.Sessions(gp = raceDateTime)
        }

    private fun RaceRemote.getRaceTime(): Instant {
        val raceDateTime = if (timeInitialized) {
            ZonedDateTime.parse("${date}T$time").toInstant()
        } else {
            ZonedDateTime.parse("${date}T15:00:00Z").toInstant()
        }
        return raceDateTime
    }

    private suspend fun zipMrdAndF1cSessions(races: List<RaceRemote>, season: Int) =
        races.zip(f1calendarService.getRaces(season).races) { mrd, f1c ->
            mrd.sessions = f1c.sessions
            mrd
        }

    suspend fun getResults(season: Int, round: Int): List<ResultRemote> {
        return mrdService.getResults(
            season,
            round
        ).mrData.table.races.firstOrNull()?.results ?: listOf()
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

    private fun getWikipediaTitle(url: String): String {
        return URLDecoder.decode(
            url.splitToSequence("/").last(),
            Charsets.UTF_8.name()
        )
    }

    suspend fun getLapTime(season: Int, round: Int, driver: String) =
        mrdService.getLapTimes(season, round, driver).mrData.table.races.firstOrNull()?.laps
            ?: listOf()

}
