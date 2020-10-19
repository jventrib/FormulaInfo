package com.jventrib.f1infos.race.data.db

import androidx.room.*
import com.jventrib.f1infos.race.data.RaceRepository
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.model.RaceResult
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceResultDao {

    @Query("SELECT * from race_result WHERE season = :season and round = :round ORDER BY position ASC")
    fun getRaceResults(season: Int, round: Int): Flow<List<RaceResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(races: List<RaceResult>)

    @Query("DELETE FROM race_result")
    suspend fun deleteAll()
}