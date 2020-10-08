package com.jventrib.f1infos.race.data.remote

import java.net.URLDecoder
import java.net.URLEncoder

open class RaceRemoteDataSource(
    private val raceService: RaceService,
    private val countryService: CountryService,
    private val wikipediaService: WikipediaService
) {
    open suspend fun getRaces(season: Int) = raceService.getRaces(season).mrData.table.races

    suspend fun getCountryFlag(country: String) =
        countryService.getCountry(country).last().alpha2Code.toLowerCase()

    suspend fun getCircuitImage(circuitUrl: String): String {
        val name = URLDecoder.decode(circuitUrl.splitToSequence("/").last(), Charsets.UTF_8.name())
        val pageImage = wikipediaService.getPageImage(name)
        val query = pageImage.query
        return query.pages.values.first().original.source
    }
}
