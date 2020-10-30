package com.jventrib.formulainfo.race.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jventrib.formulainfo.race.model.db.Constructor
import kotlinx.coroutines.flow.Flow

@Dao
interface ConstructorDao {

    @Query("SELECT * from constructor")
    fun getAllConstructors(): Flow<List<Constructor>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<Constructor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: Constructor)

    @Query("DELETE FROM constructor")
    suspend fun deleteAll()
}