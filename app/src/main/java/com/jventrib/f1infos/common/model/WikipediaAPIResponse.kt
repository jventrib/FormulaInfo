package com.jventrib.f1infos.common.model

data class WikipediaAPIResponse(
    val query: Query
) {
    data class Query(
        val pages: Map<Int, Page>
    ) {
        data class Page(
            val original: Image?,
            val thumbnail: Image?
        ) {
            data class Image(
                val source: String
            )
        }
    }
}
