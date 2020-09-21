package com.jventrib.f1infos.race.data.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jventrib.f1infos.common.utils.Converters
import com.jventrib.f1infos.race.model.Race
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
            context: Context,
            scope: CoroutineScope
        ): AppRoomDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context,
                    AppRoomDatabase::class.java,
                    "f1_database"
                )
                    .addCallback(RaceDatabaseCallback(scope))
//                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }

    private class RaceDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            instance?.let { database ->
                scope.launch {
                    populateDatabase(database.raceDao())
                }
            }
        }

        suspend fun populateDatabase(raceDao: RaceDao) {
            // Delete all content here.
//            raceDao.deleteAll()

//            // Add sample words.
//            var race = Race("2020", 1, "", "Austria", "2020-06-01")
//            raceDao.insert(race)
//            race = Race("2020", 2, "", "Styria", "2020-06-09")
//            raceDao.insert(race)
        }
    }
}