package com.jventrib.formulainfo.data.remote

import java.lang.reflect.Type
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit

class ThrottledCallAdapterFactory(
    private val scope: CoroutineScope,
    private val period: Duration = 250.milliseconds
) : CallAdapter.Factory() {
    private val mutex = Mutex()

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *> {
        return ThrottledCallAdapter(
            scope,
            mutex,
            period,
            retrofit.nextCallAdapter(this, returnType, annotations)
        )
    }

    class ThrottledCallAdapter<R, T>(
        private val scope: CoroutineScope,
        private val mutex: Mutex,
        private val period: Duration,
        private val nextCallAdapter: CallAdapter<R, T>
    ) : CallAdapter<R, T> by nextCallAdapter {
        override fun adapt(call: Call<R>): T {
            return if (call.request().url.host == "api.jolpi.ca")
                nextCallAdapter.adapt(ThrottledCall(scope, mutex, period, call))
            else
                nextCallAdapter.adapt(call)
        }
    }
}