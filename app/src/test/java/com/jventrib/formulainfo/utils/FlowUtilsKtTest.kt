package com.jventrib.formulainfo.utils

import com.google.common.truth.Truth
import java.time.Instant
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FlowUtilsKtTest {

    @Test
    fun countDownFlow() {
        runBlocking {
            val futurDate = Instant.now().plusSeconds(66666)
            val countDownFlow = futurDate.countDownFlow(1.seconds)
            val toList = countDownFlow
                .take(5)
                .onEach {
                    it.toComponents { days, hours, minutes, seconds, _ ->
                        println("$days days $hours:$minutes:$seconds")
                    }
                }
                .toList()
            toList[3].toComponents { days, hours, minutes, seconds, nanoseconds ->
                Truth.assertThat(days).isEqualTo(0)
                Truth.assertThat(hours).isEqualTo(18)
                Truth.assertThat(minutes).isEqualTo(31)
                Truth.assertThat(seconds).isEqualTo(2)
            }
            toList[4].toComponents { days, hours, minutes, seconds, nanoseconds ->
                Truth.assertThat(days).isEqualTo(0)
                Truth.assertThat(hours).isEqualTo(18)
                Truth.assertThat(minutes).isEqualTo(31)
                Truth.assertThat(seconds).isEqualTo(1)
            }
        }
    }
}
