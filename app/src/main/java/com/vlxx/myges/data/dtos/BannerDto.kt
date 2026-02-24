package com.vlxx.myges.data.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BannerApiResponseDto(
    @Json(name = "result") val result: BannerResultDto
)

@JsonClass(generateAdapter = true)
data class BannerResultDto(
    @Json(name = "content") val content: List<BannerDto>
)

@JsonClass(generateAdapter = true)
data class BannerDto(
    @Json(name = "ba_id") val bannerId: Int?,
    @Json(name = "display_order") val displayOrder: Int?,
    @Json(name = "title") val title: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "author") val author: String?,
    @Json(name = "html") val html: String?,
    @Json(name = "image") val image: String?,
    @Json(name = "begin_date") val beginDate: Long?,
    @Json(name = "end_date") val endDate: Long?,
    @Json(name = "url") val url: String?
)
