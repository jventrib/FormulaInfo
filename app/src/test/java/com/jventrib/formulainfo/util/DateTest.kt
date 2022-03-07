package com.jventrib.formulainfo.util

import com.google.common.truth.Truth
import com.jventrib.formulainfo.ui.common.toDuration
import org.junit.Test

class DateTest {
    @Test
    fun testDateParse() {
        val time = "2:25.216".toDuration()
        val time2 = "1:01:34.017".toDuration()

        Truth.assertThat(time).isEqualTo(145216)
        Truth.assertThat(time2).isEqualTo(3694017)
    }
}
