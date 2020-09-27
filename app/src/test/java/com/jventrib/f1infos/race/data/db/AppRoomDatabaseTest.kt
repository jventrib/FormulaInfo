package com.jventrib.f1infos.race.data.db

import com.google.common.truth.Truth
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

class AppRoomDatabaseTest {

    @Test
    fun getDatabaseTest() {

        val database = AppRoomDatabase.getDatabase(mockk(), mockk())

        Truth.assertThat(database).isNotNull()
    }
}