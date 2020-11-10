package com.jventrib.formulainfo.race.data.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.jventrib.formulainfo.race.model.db.Circuit
import com.jventrib.formulainfo.race.model.db.Race

interface IDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<T>)

}
