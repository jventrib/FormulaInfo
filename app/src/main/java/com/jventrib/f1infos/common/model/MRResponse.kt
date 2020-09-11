package com.jventrib.f1infos.common.model

import com.google.gson.annotations.SerializedName

data class MRResponse<T>(
    @SerializedName("MRData")
    val mrData: MRData<T>
) {
    data class MRData<T>(
        val total: Int,
        @SerializedName("RaceTable")
        val table: T
    )
}