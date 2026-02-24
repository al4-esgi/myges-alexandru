package com.vlxx.myges.domain.repositories

import com.vlxx.myges.data.dtos.ProfileResponseDto

interface ProfileRepository {
    suspend fun getProfile(): ProfileResponseDto
}
