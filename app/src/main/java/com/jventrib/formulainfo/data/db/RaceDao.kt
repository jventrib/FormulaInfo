package com.jventrib.formulainfo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.RaceInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceDao {

    @Transaction
    @Query("SELECT * from race WHERE season = :season AND round = :round")
    fun getRace(season: Int, round: Int): Flow<Race>

    @Transaction
    @Query("SELECT * from race WHERE season = :season ORDER BY round ASC")
    fun getRaces(season: Int): Flow<List<Race>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(raceInfos: List<RaceInfo>)

    @Query("DELETE FROM race")
    suspend fun deleteAll()

    @Query("DELETE FROM race WHERE season >= :season")
    suspend fun deleteSeason(season: Int)
}
