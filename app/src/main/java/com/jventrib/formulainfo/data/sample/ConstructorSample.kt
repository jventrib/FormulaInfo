package com.jventrib.formulainfo.data.sample

import com.jventrib.formulainfo.model.db.Constructor

object ConstructorSample {
    private val constructors = mapOf(
        "alfa" to Constructor(
            "alfa",
            "http://en.wikipedia.org/wiki/Alfa_Romeo_in_Formula_One",
            "Alfa Romeo",
            "Swiss",
            null
        ),
        "alphatauri" to Constructor(
            "alphatauri",
            "http://en.wikipedia.org/wiki/Scuderia_AlphaTauri",
            "AlphaTauri",
            "Italian",
            null
        ),
        "alpine" to Constructor(
            "alpine",
            "http://en.wikipedia.org/wiki/Alpine_F1_Team",
            "Alpine F1 Team",
            "French",
            null
        ),
        "aston_martin" to Constructor(
            "aston_martin",
            "http://en.wikipedia.org/wiki/Aston_Martin_in_Formula_One",
            "Aston Martin",
            "British",
            null
        ),
        "ferrari" to Constructor(
            "ferrari",
            "http://en.wikipedia.org/wiki/Scuderia_Ferrari",
            "Ferrari",
            "Italian",
            null
        ),
        "haas" to Constructor(
            "haas",
            "http://en.wikipedia.org/wiki/Haas_F1_Team",
            "Haas F1 Team",
            "American",
            null
        ),
        "mclaren" to Constructor(
            "mclaren",
            "http://en.wikipedia.org/wiki/McLaren",
            "McLaren",
            "British",
            null
        ),
        "mercedes" to Constructor(
            "mercedes",
            "http://en.wikipedia.org/wiki/Mercedes-Benz_in_Formula_One",
            "Mercedes",
            "German",
            null
        ),
        "red_bull" to Constructor(
            "red_bull",
            "http://en.wikipedia.org/wiki/Red_Bull_Racing",
            "Red Bull",
            "Austrian",
            null
        ),
        "williams" to Constructor(
            "williams",
            "http://en.wikipedia.org/wiki/Williams_Grand_Prix_Engineering",
            "Williams",
            "British",
            null
        )

    )

    fun getAllConstructors(): List<Constructor> {
        return constructors.values.toList()
    }
}
