package com.vlxx.myges.data.network

import com.vlxx.myges.data.dtos.UserDto
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {

    @POST("auth/login")
    suspend fun login(@Body body: String): UserDto

}