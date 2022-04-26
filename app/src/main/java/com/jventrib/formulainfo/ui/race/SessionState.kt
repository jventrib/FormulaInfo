package com.jventrib.formulainfo.ui.race

import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.db.Session

data class SessionState(
    var results: List<Result>,
    val session: Session,
    val setSession: (Session) -> Unit
)
