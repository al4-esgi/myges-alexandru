package com.vlxx.myges.data.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileApiResponseDto(
    @Json(name = "response_code")
    val responseCode: Int,
    @Json(name = "version")
    val version: String,
    @Json(name = "result")
    val result: ProfileResponseDto
)

@JsonClass(generateAdapter = true)
data class ProfileResponseDto(
    @Json(name = "uid")
    val uid: Int,
    @Json(name = "student_id")
    val studentId: String?,
    @Json(name = "ine")
    val ine: String?,
    @Json(name = "civility")
    val civility: String?,
    @Json(name = "firstname")
    val firstname: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "maiden_name")
    val maidenName: String?,
    @Json(name = "birthday")
    val birthday: Long?,
    @Json(name = "birthplace")
    val birthplace: String?,
    @Json(name = "birth_country")
    val birthCountry: String?,
    @Json(name = "address1")
    val address1: String?,
    @Json(name = "address2")
    val address2: String?,
    @Json(name = "city")
    val city: String?,
    @Json(name = "zipcode")
    val zipcode: String?,
    @Json(name = "country")
    val country: String?,
    @Json(name = "telephone")
    val telephone: String?,
    @Json(name = "mobile")
    val mobile: String?,
    @Json(name = "email")
    val email: String?,
    @Json(name = "nationality")
    val nationality: String?,
    @Json(name = "personal_mail")
    val personalMail: String?,
    @Json(name = "emergency_contact")
    val emergencyContact: EmergencyContactDto?,
    @Json(name = "_links")
    val links: ProfileLinksDto?
)

@JsonClass(generateAdapter = true)
data class EmergencyContactDto(
    @Json(name = "emergency_id")
    val emergencyId: Int?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "type_details")
    val typeDetails: String?,
    @Json(name = "firstname")
    val firstname: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "telephone")
    val telephone: String?,
    @Json(name = "mobile")
    val mobile: String?,
    @Json(name = "work_phone")
    val workPhone: String?
)

@JsonClass(generateAdapter = true)
data class ProfileLinksDto(
    @Json(name = "photo")
    val photo: LinkDto?
)

@JsonClass(generateAdapter = true)
data class LinkDto(
    @Json(name = "href")
    val href: String?
)
