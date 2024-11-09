package com.jventrib.formulainfo.data.remote

import java.lang.reflect.Type
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Semaphore
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit

class ThrottledCallAdapterFactory(
    private val scope: CoroutineScope,
    concurrency: Int = 1,
    private val period: Duration = 250.milliseconds
) : CallAdapter.Factory() {
    private val semaphore: Semaphore = Semaphore(concurrency)

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *> {
        return ThrottledCallAdapter(
            scope,
            semaphore,
            period,
            retrofit.nextCallAdapter(this, returnType, annotations)
        )
    }

    class ThrottledCallAdapter<R, T>(
        private val scope: CoroutineScope,
        private val semaphore: Semaphore,
        private val period: Duration,
        private val nextCallAdapter: CallAdapter<R, T>
    ) : CallAdapter<R, T> by nextCallAdapter {
        override fun adapt(call: Call<R>): T {
            return if (call.request().url.host == "api.jolpi.ca")
                nextCallAdapter.adapt(ThrottledCall(scope, semaphore, period, call))
            else
                nextCallAdapter.adapt(call)
        }
    }
}