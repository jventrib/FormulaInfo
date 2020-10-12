package com.jventrib.f1infos.race.data.remote

import com.jventrib.f1infos.race.model.Race
import java.net.URLDecoder
import java.util.*

open class RaceRemoteDataSource(
    private val mrdService: RaceService,
    private val countryService: CountryService,
    private val wikipediaService: WikipediaService,
    private val f1calendarService: F1CalendarService,

    ) {
    suspend fun getRaces(season: Int): List<Race> = mrdService.getRaces(season).mrData.table.races
        .zip(f1calendarService.getRaces(season).races) { mrd, f1c ->
            mrd.sessions = f1c.sessions;
            mrd
        }

    suspend fun getCountryFlag(country: String) =
        countryService.getCountry(country).last().alpha2Code.toLowerCase(Locale.ROOT)

    suspend fun getCircuitImage(circuitUrl: String): String {
        val name = URLDecoder.decode(circuitUrl.splitToSequence("/").last(), Charsets.UTF_8.name())
        val pageImage = wikipediaService.getPageImage(name)
        val query = pageImage.query
        return query.pages.values.first().original.source
    }
}
