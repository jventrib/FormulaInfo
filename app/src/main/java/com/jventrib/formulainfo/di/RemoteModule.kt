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

import android.content.Context
import coil.ImageLoader
import coil.util.CoilUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.data.remote.F1CalendarService
import com.jventrib.formulainfo.data.remote.MrdService
import com.jventrib.formulainfo.data.remote.RaceRemoteDataSource
import com.jventrib.formulainfo.data.remote.WikipediaService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .readTimeout(20, TimeUnit.SECONDS)
            .cache(CoilUtils.createDefaultCache(context))
            .build()
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .okHttpClient { okHttpClient }
            .build()
    }

    @Provides
    @Singleton
    fun provideMrdService(
        okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): MrdService =
        buildRetrofit(
            okHttpClient,
            context.getString(R.string.api_ergast)
        ).create(MrdService::class.java)

    @Provides
    @Singleton
    fun provideWikipediaService(
        okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): WikipediaService =
        buildRetrofit(
            okHttpClient,
            context.getString(R.string.api_wikipedia)
        ).create(WikipediaService::class.java)

    @Provides
    @Singleton
    fun provideF1CalendarService(
        okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): F1CalendarService =
        buildRetrofit(
            okHttpClient,
            context.getString(R.string.api_github_raw)
        ).create(F1CalendarService::class.java)

    @Provides
    @Singleton
    fun provideRaceRemoteDataSource(
        mrdService: MrdService,
        wikipediaService: WikipediaService,
        f1CalendarService: F1CalendarService
    ) =
        RaceRemoteDataSource(mrdService, wikipediaService, f1CalendarService)

    private fun buildRetrofit(
        okHttpClient: OkHttpClient,
        baseUrl: String
    ) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(gsonConverterFactory)
        .client(okHttpClient)
        .build()


    private val gsonConverterFactory = GsonConverterFactory.create(
        GsonBuilder().registerTypeAdapter(
            Instant::class.java,
            JsonDeserializer { json, _, _ ->
                ZonedDateTime.parse(json.asJsonPrimitive.asString).toInstant()
            }).create()
    )

}
