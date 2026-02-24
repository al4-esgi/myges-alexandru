package com.vlxx.myges.data.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileResponseDto(
    @Json(name = "result")
    val result: ProfileDto
)

@JsonClass(generateAdapter = true)
data class ProfileDto(
    @Json(name = "uid")
    val uid: String,
    @Json(name = "email")
    val email: String?,
    @Json(name = "firstname")
    val firstname: String?,
    @Json(name = "lastname")
    val lastname: String?,
    @Json(name = "civility")
    val civility: String?
)
