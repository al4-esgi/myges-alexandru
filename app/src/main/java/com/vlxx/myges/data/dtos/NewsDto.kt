package com.vlxx.myges.data.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewsApiResponseDto(
    @Json(name = "result") val result: NewsResultDto
)

@JsonClass(generateAdapter = true)
data class NewsResultDto(
    @Json(name = "content") val content: List<NewsDto>
)

@JsonClass(generateAdapter = true)
data class NewsDto(
    @Json(name = "ne_id") val newsId: Int?,
    @Json(name = "title") val title: String?,
    @Json(name = "author") val author: String?,
    @Json(name = "summary") val summary: String?,
    @Json(name = "text") val text: String?,
    @Json(name = "html") val html: String?,
    @Json(name = "date") val date: Long?,
    @Json(name = "update_date") val updateDate: Long?
)
