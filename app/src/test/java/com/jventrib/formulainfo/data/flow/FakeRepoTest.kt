package com.jventrib.formulainfo.data.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class FakeRepoTest {

    @Test
    fun testRepoRaces() {
        runBlocking {
            getRaces().collect {
                println(it)
            }
        }
    }

    @Test
    fun testRepoResults() {
        runBlocking {
            getResults(1).collect {
                println(it)
            }
        }
    }

    @Test
    fun testRepoRaceWithResults() {
        runBlocking {
//            val last = getRacesWithResults().last()
//            println(last)
            val list = getRacesWithResults()
                .onEach {
                    println(it)
                }
                .toList()
            println(list.size)
            val last = list.last()
            println(last)
            Assert.assertTrue(
                last.all { race ->
                    race.fakeRace.flag != null && race.fakeResults.all { it.driver != null }
                }
            )

//            getRacesWithResults().collect {
//                println(it)
//            }
        }
    }

    private fun getRacesWithResults(): Flow<List<FakeRaceWithResults>> {

        val f1 = getRaces().map { list ->
            list.map { FakeRaceWithResults(it, listOf()) }
        }

        val f2 = getRaces()
            .take(1)
            .flatMapLatest { it.asFlow() }
            .flatMapMerge(100) { race ->
                getResults(race.round).map {
                    FakeRaceWithResults(race, it)
                }
            }
            .scan(mapOf<Int, FakeRaceWithResults>()) { acc, value ->
                acc + (value.fakeRace.round to value)
            }.map { it.values.toList() }

        return f1.combine(f2) { a, b ->
            a.zip(b) { za, zb ->
                za.copy(fakeResults = zb.fakeResults)
            }
        }
    }
//    val r = getRaces()
//        .flatMapLatest {
//            it.asFlow()
//        }
//        .flatMapMerge(1000) { race ->
//            getResults(race.round).map {
//                FakeRaceWithResults(race, it)
//            }
//        }
//        .debounce(30)

//            .onEach { println("flow: $it") }

//        val r = getRaces()
//            .transformLatest { races ->
//                races.forEach { race ->
//                    emit(FakeRaceWithResults(race, listOf()))
//                }
//                races.map { race ->
//                    val rrFlow = getResults(race.round)
//                        .map { FakeRaceWithResults(race, it) }
//                    emitAll(rrFlow)
//                }
//
//            }
//        .scan(mapOf<Int, FakeRaceWithResults>()) { acc, value ->
//            val existing = acc[value.fakeRace.round]
//                val results =
//                    if (existing != null && existing.fakeResults.size > 1)
//                        existing.fakeResults
//                    else
//                    value.fakeResults
//                acc + (value.fakeRace.round to value.copy(fakeResults = results))
//                println("value: $value")
//            acc + (value.fakeRace.round to value)
//        }

//    return r.map
//    { it.values.toList() }

    private fun getRaces() = flow {
        emit(races)
        delay(100)

        (1..22).forEach { loadIndex ->
            delay(100)
            emit(
                races.mapIndexed { index, fakeRace ->
                    if (index < loadIndex) fakeRace.copy(flag = "Loaded") else fakeRace
                }
            )
        }
    }

    private fun getResults(round: Int) = flow {
        emit(results.map { it.copy(key = "$round-${it.key}") })
        delay(100)

        (1..20).forEach { loadIndex ->
            delay(100)
            val fakeResult = results.mapIndexed { index, fakeResult ->
                if (index < loadIndex) fakeResult.copy(
                    key = "$round-${fakeResult.key}",
                    driver = "Img"
                ) else fakeResult.copy(key = "$round-${fakeResult.key}")
            }
            emit(fakeResult)
        }
    }

    private val races = (1..22).map { FakeRace(it, null) }

    private val results = (1..20).map { FakeResult(it.toString(), null) }

    data class FakeRace(
        val round: Int,
        val flag: String?
    ) {
        override fun toString(): String {
            return "($round->$flag)"
        }
    }

    data class FakeResult(
        val key: String,
        val driver: String?

    ) {
        override fun toString(): String {
            return "($key->$driver)"
        }
    }

    data class FakeRaceWithResults(
        val fakeRace: FakeRace,
        val fakeResults: List<FakeResult>
    ) {
        override fun toString(): String {
            return "(fakeRace=$fakeRace, fakeResults=$fakeResults)"
        }
    }
//    data class FakeDriver(
//        val name: String,
//        val image: String?
//    )
}
