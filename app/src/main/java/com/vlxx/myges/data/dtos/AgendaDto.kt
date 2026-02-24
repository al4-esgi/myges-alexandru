package com.vlxx.myges.data.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgendaApiResponseDto(
    @Json(name = "result") val result: List<AgendaEventDto>
)

@JsonClass(generateAdapter = true)
data class AgendaEventDto(
    @Json(name = "reservation_id") val reservationId: Int?,
    @Json(name = "name") val name: String?,
    @Json(name = "type") val type: String?,
    @Json(name = "modality") val modality: String?,
    @Json(name = "start_date") val startDate: Long?,
    @Json(name = "end_date") val endDate: Long?,
    @Json(name = "rooms") val rooms: List<RoomDto>?,
    @Json(name = "discipline") val discipline: DisciplineDto?,
    @Json(name = "teacher") val teacher: String?,
    @Json(name = "promotion") val promotion: String?,
    @Json(name = "comment") val comment: String?
)

@JsonClass(generateAdapter = true)
data class RoomDto(
    @Json(name = "room_id") val roomId: Int?,
    @Json(name = "name") val name: String?,
    @Json(name = "floor") val floor: String?,
    @Json(name = "campus") val campus: String?,
    @Json(name = "color") val color: String?,
    @Json(name = "latitude") val latitude: String?,
    @Json(name = "longitude") val longitude: String?
)

@JsonClass(generateAdapter = true)
data class DisciplineDto(
    @Json(name = "name") val name: String?,
    @Json(name = "teacher") val teacher: String?,
    @Json(name = "trimester") val trimester: String?,
    @Json(name = "year") val year: Int?,
    @Json(name = "student_group_name") val studentGroupName: String?
)
