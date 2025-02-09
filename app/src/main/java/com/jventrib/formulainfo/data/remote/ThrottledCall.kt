package com.jventrib.formulainfo.data.remote

import com.jventrib.formulainfo.utils.throttled
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import retrofit2.Call
import retrofit2.Callback

class ThrottledCall<R>(
    private val scope: CoroutineScope,
    private val mutex: Mutex,
    private val period: Duration,
    private val call: Call<R>
) :
    Call<R> by call {
    override fun enqueue(callback: Callback<R>) {
        scope.launch {
            throttled(mutex, period) {
                println("enqueuing ${call.request()}")
                call.enqueue(callback)
            }
        }
    }
}
