package com.jventrib.formulainfo.data.sample

import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.db.ResultInfo

object ResultSample {


    fun getResults(): List<Result> {
        return listOf(
            result(
                44,
                1,
                25f,
                "hamilton",
                "mercedes",
                2,
                56,
                "Finished"
            ),
            result(
                33,
                2,
                18f,
                "max_verstappen",
                "red_bull",
                1,
                56,
                "Finished"
            ),
            result(
                77,
                3,
                16f,
                "bottas",
                "mercedes",
                3,
                56,
                "Finished"
            ),
            result(
                4,
                4,
                12f,
                "norris",
                "mclaren",
                7,
                56,
                "Finished"
            ),

            result(
                11,
                5,
                10f,
                "perez",
                "red_bull",
                0,
                56,
                "Finished"
            ),

            result(
                16,
                6,
                8f,
                "leclerc",
                "ferrari",
                4,
                56,
                "Finished"
            ),
            result(
                3,
                7,
                6f,
                "ricciardo",
                "mclaren",
                6,
                56,
                "Finished"
            ),
            result(
                55,
                8,
                4f,
                "sainz",
                "ferrari",
                8,
                56,
                "Finished"
            ),
            result(
                22,
                9,
                2f,
                "tsunoda",
                "alphatauri",
                13,
                56,
                "Finished"
            ),
            result(
                18,
                10,
                1f,
                "stroll",
                "aston_martin",
                10,
                56,
                "Finished"
            ),
            result(
                7,
                11,
                0f,
                "raikkonen",
                "alfa",
                14,
                56,
                "Finished"
            ),
            result(
                99,
                12,
                0f,
                "giovinazzi",
                "alfa",
                12,
                55,
                "+1 Lap"
            ),
            result(
                31,
                13,
                0f,
                "ocon",
                "alpine",
                16,
                55,
                "+1 Lap"
            ),
            result(
                63,
                14,
                0f,
                "russell",
                "williams",
                15,
                55,
                "+1 Lap"
            ),
            result(
                5,
                15,
                0f,
                "vettel",
                "aston_martin",
                20,
                55,
                "+1 Lap"
            ),
            result(
                47,
                16,
                0f,
                "mick_schumacher",
                "haas",
                18,
                55,
                "+1 Lap"
            ),
            result(
                10,
                17,
                0f,
                "gasly",
                "alphatauri",
                5,
                52,
                "Retired"
            ),
            result(
                6,
                18,
                0f,
                "latifi",
                "williams",
                17,
                51,
                "Retired"
            ),
            result(
                14,
                19,
                0f,
                "alonso",
                "alpine",
                9,
                32,
                "Brakes"
            ),
            result(
                9,
                20,
                0f,
                "mazepin",
                "haas",
                19,
                0,
                "Accident"
            )


        )

    }

    private fun result(
        number: Int,
        position: Int,
        points: Float,
        driverId: String,
        constructorId: String,
        grid: Int,
        laps: Int,
        status: String
    ) =
        Result(
            getResultInfo(driverId, number, position, points),
            DriverSample.drivers[driverId]!!,
            ConstructorSample.constructors[constructorId]!!
        )

    fun getResultInfo(driverId: String, number: Int, position: Int, points: Float) =
        ResultInfo(
            "2021-1-$driverId",
            2021,
            1,
            number,
            position,
            position.toString(),
            points,
            driverId,
            "cid",
            1,
            50,
            "",
            null,
            null
        )

}