package com.jventrib.formulainfo.data.db

import androidx.room.*
import com.jventrib.formulainfo.model.db.Lap
import kotlinx.coroutines.flow.Flow

@Dao
interface LapTimeDao {

    @Transaction
    @Query("SELECT * from lap_time WHERE season = :season and round = :round and driver = :driver ORDER BY number ASC")
    fun getAll(season: Int, round: Int, driver: String): Flow<List<Lap>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(laps: List<Lap>)

    @Query("DELETE FROM lap_time")
    suspend fun deleteAll()
}