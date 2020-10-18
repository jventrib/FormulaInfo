package com.jventrib.f1infos.race.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jventrib.f1infos.common.utils.Converters
import com.jventrib.f1infos.race.model.Race

@Database(entities = [Race::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun raceDao(): RaceDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var instance: AppRoomDatabase? = null

        fun getDatabase(
            context: Context
        ): AppRoomDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context,
                    AppRoomDatabase::class.java,
                    "f1_database"
                ).build().also { instance = it }
            }
    }
}