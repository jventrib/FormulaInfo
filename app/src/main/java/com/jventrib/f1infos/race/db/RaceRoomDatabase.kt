package com.jventrib.f1infos.race.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jventrib.f1infos.race.model.Race
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Race::class], version = 1, exportSchema = false)
public abstract class RaceRoomDatabase : RoomDatabase() {

    abstract fun raceDao(): RaceDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: RaceRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): RaceRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RaceRoomDatabase::class.java,
                    "race_database"
                )
                    .addCallback(RaceDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class RaceDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.raceDao())
                }
            }
        }

        suspend fun populateDatabase(wordDao: RaceDao) {
            // Delete all content here.
            wordDao.deleteAll()

            // Add sample words.
            var race = Race("2020", 1, "", "Austria", "2020-06-01")
            wordDao.insert(race)
            race = Race("2020", 2, "", "Styria", "2020-06-09")
            wordDao.insert(race)
        }
    }
}