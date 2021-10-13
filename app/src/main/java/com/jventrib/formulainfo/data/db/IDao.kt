package com.jventrib.formulainfo.data.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface IDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<T>)

}
