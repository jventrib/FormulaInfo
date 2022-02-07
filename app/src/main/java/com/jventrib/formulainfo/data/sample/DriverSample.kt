package com.jventrib.formulainfo.data.sample

import com.jventrib.formulainfo.model.db.Driver

object DriverSample {

    private val drivers = mapOf(
        "alonso" to Driver(
            "alonso",
            14,
            "ALO",
            "http://en.wikipedia.org/wiki/Fernando_Alonso",
            "Fernando",
            "Alonso",
            "1981-07-29",
            "Spanish",
            null,
            null,
            0
        ),

        "bottas" to Driver(
            "bottas",
            77,
            "BOT",
            "http://en.wikipedia.org/wiki/Valtteri_Bottas",
            "Valtteri",
            "Bottas",
            "1989-08-28",
            "Finnish",
            null,
            null,
            0
        ),
        "gasly" to Driver(
            "gasly",
            10,
            "GAS",
            "http://en.wikipedia.org/wiki/Pierre_Gasly",
            "Pierre",
            "Gasly",
            "1996-02-07",
            "French",
            null,
            null,
            0
        ),
        "giovinazzi" to Driver(
            "giovinazzi",
            99,
            "GIO",
            "http://en.wikipedia.org/wiki/Antonio_Giovinazzi",
            "Antonio",
            "Giovinazzi",
            "1993-12-14",
            "Italian",
            null,
            null,
            0
        ),
        "hamilton" to Driver(
            "hamilton",
            44,
            "HAM",
            "http://en.wikipedia.org/wiki/Lewis_Hamilton",
            "Lewis",
            "Hamilton",
            "1985-01-07",
            "British",
            null,
            null,
            0
        ),
        "kubica" to Driver(
            "kubica",
            88,
            "KUB",
            "http://en.wikipedia.org/wiki/Robert_Kubica",
            "Robert",
            "Kubica",
            "1984-12-07",
            "Polish",
            null,
            null,
            0
        ),
        "latifi" to Driver(
            "latifi",
            6,
            "LAT",
            "http://en.wikipedia.org/wiki/Nicholas_Latifi",
            "Nicholas",
            "Latifi",
            "1995-06-29",
            "Canadian",
            null,
            null,
            0
        ),
        "leclerc" to Driver(
            "leclerc",
            16,
            "LEC",
            "http://en.wikipedia.org/wiki/Charles_Leclerc",
            "Charles",
            "Leclerc",
            "1997-10-16",
            "Monegasque",
            null,
            null,
            0
        ),
        "mazepin" to Driver(
            "mazepin",
            9,
            "MAZ",
            "http://en.wikipedia.org/wiki/Nikita_Mazepin",
            "Nikita",
            "Mazepin",
            "1999-03-02",
            "Russian",
            null,
            null,
            0
        ),
        "norris" to Driver(
            "norris",
            4,
            "NOR",
            "http://en.wikipedia.org/wiki/Lando_Norris",
            "Lando",
            "Norris",
            "1999-11-13",
            "British",
            null,
            null,
            0
        ),
        "ocon" to Driver(
            "ocon",
            31,
            "OCO",
            "http://en.wikipedia.org/wiki/Esteban_Ocon",
            "Esteban",
            "Ocon",
            "1996-09-17",
            "French",
            null,
            null,
            0
        ),
        "perez" to Driver(
            "perez",
            11,
            "PER",
            "http://en.wikipedia.org/wiki/Sergio_P%C3%A9rez",
            "Sergio",
            "Pérez",
            "1990-01-26",
            "Mexican",
            null,
            null,
            0
        ),
        "raikkonen" to Driver(
            "raikkonen",
            7,
            "RAI",
            "http://en.wikipedia.org/wiki/Kimi_R%C3%A4ikk%C3%B6nen",
            "Kimi",
            "Räikkönen",
            "1979-10-17",
            "Finnish",
            null,
            null,
            0
        ),
        "ricciardo" to Driver(
            "ricciardo",
            3,
            "RIC",
            "http://en.wikipedia.org/wiki/Daniel_Ricciardo",
            "Daniel",
            "Ricciardo",
            "1989-07-01",
            "Australian",
            null,
            null,
            0
        ),
        "russell" to Driver(
            "russell",
            63,
            "RUS",
            "http://en.wikipedia.org/wiki/George_Russell_%28racing_driver%29",
            "George",
            "Russell",
            "1998-02-15",
            "British",
            null,
            null,
            0
        ),
        "sainz" to Driver(
            "sainz",
            55,
            "SAI",
            "http://en.wikipedia.org/wiki/Carlos_Sainz_Jr.",
            "Carlos",
            "Sainz",
            "1994-09-01",
            "Spanish",
            null,
            null,
            0
        ),
        "mick_schumacher" to Driver(
            "mick_schumacher",
            47,
            "MSC",
            "http://en.wikipedia.org/wiki/Mick_Schumacher",
            "Mick",
            "Schumacher",
            "1999-03-22",
            "German",
            null,
            null,
            0
        ),
        "stroll" to Driver(
            "stroll",
            18,
            "STR",
            "http://en.wikipedia.org/wiki/Lance_Stroll",
            "Lance",
            "Stroll",
            "1998-10-29",
            "Canadian",
            null,
            null,
            0
        ),
        "tsunoda" to Driver(
            "tsunoda",
            22,
            "TSU",
            "http://en.wikipedia.org/wiki/Yuki_Tsunoda",
            "Yuki",
            "Tsunoda",
            "2000-05-11",
            "Japanese",
            null,
            null,
            0
        ),
        "max_verstappen" to Driver(
            "max_verstappen",
            33,
            "VER",
            "http://en.wikipedia.org/wiki/Max_Verstappen",
            "Max",
            "Verstappen",
            "1997-09-30",
            "Dutch",
            null,
            null,
            0
        ),
        "vettel" to Driver(
            "vettel",
            5,
            "VET",
            "http://en.wikipedia.org/wiki/Sebastian_Vettel",
            "Sebastian",
            "Vettel",
            "1987-07-03",
            "German",
            null,
            null,
            0
        )
    )

    fun getAllDrivers(): List<Driver> {
        return drivers.values.toList()
    }
}
