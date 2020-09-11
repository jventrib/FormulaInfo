package com.jventrib.f1infos.common.data.remote

import retrofit2.Retrofit

inline fun <reified T> Retrofit.create() = this.create(T::class.java)