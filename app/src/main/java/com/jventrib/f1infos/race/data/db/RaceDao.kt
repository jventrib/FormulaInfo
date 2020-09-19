package com.jventrib.f1infos.race.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.model.SeasonRace
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceDao {

    @Query("SELECT * from race ORDER BY season ASC, round ASC")
    fun getAllRaces(): Flow<List<Race>>

    @Query("SELECT * from race WHERE season = :season ORDER BY round ASC")
    fun getSeasonRaces(season: Int): Flow<List<Race>>

    @Query("SELECT * from race WHERE season = :season and raceName = :raceName ORDER BY round ASC")
    fun getSeasonRace(season: Int, raceName: String): Flow<Race?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(races: List<Race>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(race: Race)

    @Query("DELETE FROM race")
    suspend fun deleteAll()

    @Update
    fun updateRaceFlag(r: Race)
}