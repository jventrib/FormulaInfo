package com.jventrib.formulainfo.race.data.db

import androidx.room.*
import com.jventrib.formulainfo.race.model.db.RaceResult
import com.jventrib.formulainfo.race.model.db.FullRaceResult
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceResultDao {

    @Transaction
    @Query("SELECT * from race_result WHERE season = :season and round = :round ORDER BY position ASC")
    fun getFullRaceResults(season: Int, round: Int): Flow<List<FullRaceResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(raceRemotes: List<RaceResult>)

    @Query("DELETE FROM race_result")
    suspend fun deleteAll()
}