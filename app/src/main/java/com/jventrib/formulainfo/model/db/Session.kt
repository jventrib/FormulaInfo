package com.jventrib.formulainfo.model.db

enum class Session(val field: String, val label: String) {
    FP1("fp1", "Free practice 1"),
    FP2("fp2", "Free practice 2"),
    FP3("fp3", "Free practice 3"),
    QUAL("qual", "Qualification"),
    SPRINT("sprint", "Sprint Qualification"),
    RACE("race", "Race");

    companion object {
        fun fromField(field: String) = Session.values().first { it.name == field }
    }
}
