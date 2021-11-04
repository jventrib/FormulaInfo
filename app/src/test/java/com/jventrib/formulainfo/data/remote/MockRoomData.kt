package com.jventrib.formulainfo.data.remote

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.jventrib.formulainfo.data.db.*
import io.mockk.mockk

class MockRoomData(
    val resultDao: ResultDao,
    val lapDao: LapDao
) : AppRoomDatabase() {
    override fun raceDao(): RaceDao {
        return mockk()
    }

    override fun circuitDao(): CircuitDao {
        return mockk()
    }

    override fun resultDao(): ResultDao {
        return resultDao
    }

    override fun driverDao(): DriverDao {
        return mockk()
    }

    override fun constructorDao(): ConstructorDao {
        return mockk()
    }

    override fun lapTimeDao(): LapDao {
        return lapDao
    }

    override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper {
        TODO("Not yet implemented")
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        return mockk()
    }

    override fun clearAllTables() {
        TODO("Not yet implemented")
    }
}