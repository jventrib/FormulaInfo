package com.jventrib.formulainfo.race.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jventrib.formulainfo.race.model.Race
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceDao {

    @Query("SELECT * from race WHERE season = :season ORDER BY round ASC")
    fun getSeasonRaces(season: Int): Flow<List<Race>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(races: List<Race>)

    @Query("DELETE FROM race")
    suspend fun deleteAll()
}