package com.jventrib.formulainfo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jventrib.formulainfo.model.db.FullResult
import com.jventrib.formulainfo.model.db.Result
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {

    @Query("SELECT * from race_result WHERE season = :season and round = :round and driverId = :driverId")
    fun getFullResult(season: Int, round: Int, driverId: String): Flow<FullResult>

    @Query("SELECT * from race_result WHERE season = :season and round = :round ORDER BY position ASC")
    fun getFullResults(season: Int, round: Int): Flow<List<FullResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(raceRemotes: List<Result>)

    @Query("DELETE FROM race_result")
    suspend fun deleteAll()
}