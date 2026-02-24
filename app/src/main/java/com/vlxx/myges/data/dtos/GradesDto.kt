package com.vlxx.myges.data.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradesApiResponseDto(
    @Json(name = "result") val result: List<CourseDto>
)

@JsonClass(generateAdapter = true)
data class CourseDto(
    @Json(name = "course") val course: String?,
    @Json(name = "code") val code: String?,
    @Json(name = "grades") val grades: List<Double>?,
    @Json(name = "bonus") val bonus: Double?,
    @Json(name = "exam") val exam: Double?,
    @Json(name = "average") val average: Double?,
    @Json(name = "trimester") val trimester: Int?,
    @Json(name = "trimester_name") val trimesterName: String?,
    @Json(name = "year") val year: Int?,
    @Json(name = "rc_id") val rcId: Int?,
    @Json(name = "ects") val ects: String?,
    @Json(name = "coef") val coef: String?,
    @Json(name = "teacher_civility") val teacherCivility: String?,
    @Json(name = "teacher_first_name") val teacherFirstName: String?,
    @Json(name = "teacher_last_name") val teacherLastName: String?,
    @Json(name = "absences") val absences: Int?,
    @Json(name = "lates") val lates: Int?,
    @Json(name = "letter_mark") val letterMark: String?,
    @Json(name = "ccaverage") val ccAverage: Double?
)
