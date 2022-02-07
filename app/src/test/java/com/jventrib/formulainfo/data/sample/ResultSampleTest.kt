package com.jventrib.formulainfo.data.sample

import com.jventrib.formulainfo.model.db.Lap
import com.jventrib.formulainfo.model.db.Result
import junit.framework.TestCase
import org.junit.Test
import java.time.Duration

class ResultSampleTest : TestCase() {

    @Test
    fun testtestFile() {
        println(getPositionLaps(0).map { it?.let { "${it.position}:${it.driverCode}" } })
        println(getPositionLaps(1).map { it?.let { "${it.position}:${it.driverCode}" } })
    }

    private fun getPositionLaps(lap: Int): List<Lap?> {
        val lapsWithStart = getLapsWithStart(ResultSample.getLapsPerResults())
        val results = lapsWithStart
            .map { entry -> entry.value.firstOrNull { it.number == lap } }
            .associateBy { it?.position }

        val indices = lapsWithStart.entries.indices
        return indices.map { results[it + 1] }
    }


    private fun getLapsWithStart(lapsByResult: Map<Result, List<Lap>>) =
        lapsByResult
            .mapValues { entry ->
                entry.value
                    .toMutableList().apply {
                        if (entry.key.resultInfo.grid != 0) {
                            add(
                                0, Lap(
                                    entry.key.resultInfo.season,
                                    entry.key.resultInfo.round,
                                    entry.key.driver.driverId,
                                    entry.key.driver.code ?: entry.key.driver.driverId,
                                    0,
                                    entry.key.resultInfo.grid,
                                    Duration.ZERO,
                                    Duration.ZERO
                                )
                            )
                        }
                    }
            }

}