package com.jventrib.f1infos.common.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.jventrib.f1infos.common.data.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun <T, A> performGetOperation(databaseQuery: () -> Flow<T>,
                               networkCall: suspend () -> Resource<A>,
                               saveCallResult: suspend (A) -> Unit): Flow<Resource<T>> =
    flow {
        emit(Resource.loading())
        val query = databaseQuery()
        val source = query.map { Resource.success(it) }
        emitAll(source)

        val responseStatus = networkCall()
        if (responseStatus.status == Resource.Status.SUCCESS) {
            saveCallResult(responseStatus.data!!)

        } else if (responseStatus.status == Resource.Status.ERROR) {
            emit(Resource.error(responseStatus.message!!))
            emitAll(source)
        }
    }