package com.jventrib.f1infos.race.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jventrib.f1infos.race.model.Race

@Database(entities = [Race::class], version = 1, exportSchema = false)
public abstract class RaceRoomDatabase : RoomDatabase() {

    abstract fun raceDao(): RaceDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: RaceRoomDatabase? = null

        fun getDatabase(context: Context): RaceRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RaceRoomDatabase::class.java,
                    "race_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}