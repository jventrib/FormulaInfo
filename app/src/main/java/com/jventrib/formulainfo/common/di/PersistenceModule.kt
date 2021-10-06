/*
 * Designed and developed by 2020 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.disneycompose.di

import android.app.Application
import androidx.room.Room
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.race.data.db.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

  @Provides
  @Singleton
  fun provideAppDatabase(application: Application): AppRoomDatabase {
    return Room
      .databaseBuilder(
        application,
        AppRoomDatabase::class.java,
        application.getString(R.string.database)
      )
      .fallbackToDestructiveMigration()
      .build()
  }

  @Provides
  @Singleton
  fun provideRaceDao(appDatabase: AppRoomDatabase): RaceDao {
    return appDatabase.raceDao()
  }

  @Provides
  @Singleton
  fun provideCircuitDao(appDatabase: AppRoomDatabase): CircuitDao {
    return appDatabase.circuitDao()
  }

  @Provides
  @Singleton
  fun provideRaceResultDao(appDatabase: AppRoomDatabase): RaceResultDao {
    return appDatabase.raceResultDao()
  }

  @Provides
  @Singleton
  fun provideDriverDao(appDatabase: AppRoomDatabase): DriverDao {
    return appDatabase.driverDao()
  }

  @Provides
  @Singleton
  fun provideConstructorDao(appDatabase: AppRoomDatabase): ConstructorDao {
    return appDatabase.constructorDao()
  }

}
