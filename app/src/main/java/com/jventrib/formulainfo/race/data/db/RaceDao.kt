package com.jventrib.formulainfo.race.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jventrib.formulainfo.race.model.db.Race
import com.jventrib.formulainfo.race.model.db.FullRace
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceDao {

    @Query("SELECT * from race WHERE season = :season ORDER BY round ASC")
    fun getSeasonRaces(season: Int): Flow<List<FullRace>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(races: List<Race>)

    @Query("DELETE FROM race")
    suspend fun deleteAll()
}