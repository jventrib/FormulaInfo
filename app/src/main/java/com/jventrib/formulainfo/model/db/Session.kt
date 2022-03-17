package com.jventrib.formulainfo.model.db

enum class Session(field: String) {
    FP1("fp1"), FP2("fp2"), FP3("fp3"), QUAL("qual"), SPRINT("sprint"), RACE("race");

    companion object {
        fun fromField(field: String) = Session.values().first { it.name == field }
    }
}
