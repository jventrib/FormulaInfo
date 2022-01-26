package com.jventrib.formulainfo.model.aggregate

import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.Result

data class RaceWithResult(val race: Race, val result: List<Result>)