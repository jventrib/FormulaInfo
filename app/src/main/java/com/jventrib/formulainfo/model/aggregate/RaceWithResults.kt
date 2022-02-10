package com.jventrib.formulainfo.model.aggregate

import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result

data class RaceWithResults(val race: Race, val results: List<Result>)
