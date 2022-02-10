package com.jventrib.formulainfo.data.db

import androidx.room.Dao
import androidx.room.Query
import com.jventrib.formulainfo.model.db.Circuit

@Dao
interface CircuitDao : IDao<Circuit> {

    @Query("DELETE FROM circuit")
    suspend fun deleteAll()
}
