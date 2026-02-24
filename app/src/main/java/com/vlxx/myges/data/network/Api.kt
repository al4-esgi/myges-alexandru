package com.vlxx.myges.data.network

import com.vlxx.myges.data.dtos.AgendaApiResponseDto
import com.vlxx.myges.data.dtos.ProfileApiResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface Api {

    @GET("https://authentication.kordis.fr/oauth/authorize?response_type=token&client_id=skolae-app")
    suspend fun authenticate(@Header("Authorization") basicAuth: String): Response<Unit>

    @GET("me/profile")
    suspend fun getProfile(): ProfileApiResponseDto

    @GET("me/agenda")
    suspend fun getAgenda(
        @Query("start") start: Long,
        @Query("end") end: Long
    ): AgendaApiResponseDto

}
