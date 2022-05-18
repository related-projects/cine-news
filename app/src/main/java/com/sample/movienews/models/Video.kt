package com.sample.movienews.models

import com.squareup.moshi.Json

data class Video (
    @Json(name = "id") val id: String?,
    @Json(name = "iso_639_1") val iso6391: String?,
    @Json(name = "iso_3166_1") val iso31661: String?,
    val name: String?,
    val key: String?,
    val site: String?,
    val size: Int?,
    val type: String?,
    val official: Boolean?,
    @Json(name = "published_at") val publishedAt: String?
)