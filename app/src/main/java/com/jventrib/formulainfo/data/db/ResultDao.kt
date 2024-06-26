package com.jventrib.formulainfo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.db.ResultInfo
import com.jventrib.formulainfo.model.db.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {

    @Transaction
    @Query("SELECT * from race_result WHERE season = :season and round = :round and driverId = :driverId")
    fun getResult(season: Int, round: Int, driverId: String): Flow<Result>

    @Transaction
    @Query("SELECT * from race_result WHERE season = :season and round = :round and session = :session ORDER BY position ASC")
    fun getResults(season: Int, round: Int, session: Session): Flow<List<Result>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(raceRemotes: List<ResultInfo>)

    @Query("DELETE FROM race_result WHERE season = :season ")
    suspend fun deleteSeason(season: Int)

    @Query("DELETE FROM race_result WHERE number = -1 and season = :season and round <= :round")
    fun deleteCurrentSeasonPastRaces(season: Int, round: Int)
}
