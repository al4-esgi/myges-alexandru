package com.vlxx.myges.data.repositories

import com.vlxx.myges.data.dtos.ProfileResponseDto
import com.vlxx.myges.data.network.Api
import com.vlxx.myges.domain.repositories.ProfileRepository
import timber.log.Timber

class ProfileRepositoryImpl(
    private val api: Api
) : ProfileRepository {

    override suspend fun getProfile(): ProfileResponseDto {
        val apiResponse = api.getProfile()
        Timber.d("Profile API Response Code: ${apiResponse.responseCode}")
        Timber.d("Profile API Version: ${apiResponse.version}")

        val profile = apiResponse.result
        Timber.d("Profile extracted from result")
        Timber.d("Profile UID: ${profile.uid}")
        Timber.d("Profile firstname: ${profile.firstname}")
        Timber.d("Profile name: ${profile.name}")
        Timber.d("Profile email: ${profile.email}")

        return profile
    }
}
