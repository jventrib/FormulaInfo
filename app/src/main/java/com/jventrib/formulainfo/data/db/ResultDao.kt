package com.jventrib.formulainfo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.db.ResultInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {

    @Query("SELECT * from race_result WHERE season = :season and round = :round and driverId = :driverId")
    fun getResult(season: Int, round: Int, driverId: String): Flow<Result>

    @Query("SELECT * from race_result WHERE season = :season and round = :round ORDER BY position ASC")
    fun getResults(season: Int, round: Int): Flow<List<Result>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(raceRemotes: List<ResultInfo>)

    @Query("DELETE FROM race_result")
    suspend fun deleteAll()
}