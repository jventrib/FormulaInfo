package com.jventrib.f1infos.race.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jventrib.f1infos.common.data.Resource
import com.jventrib.f1infos.race.model.Race
import java.io.InputStream

@Dao
interface RaceDao {

    @Query("SELECT * from race ORDER BY season ASC, round ASC")
    fun getAllRaces(): LiveData<List<Race>>

    @Query("SELECT * from race WHERE season = :season ORDER BY round ASC")
    fun getSeasonRaces(season: String): LiveData<List<Race>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(races: List<Race>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(race: Race)

    @Query("DELETE FROM race")
    suspend fun deleteAll()

    @Update
    fun updateRaceFlag(r: Race)
}