package com.sample.movienews.models

import com.squareup.moshi.Json

data class Response<T> (
    @Json(name = "id") val id: Int?,
    @Json(name = "dates") val dates: Dates?,
    @Json(name = "page") val currentPage: Int?,
    @Json(name = "results") val results: List<T>?,
    @Json(name = "total_pages") val totalPages: Int?,
    @Json(name = "total_results") val totalResults: Int?
)