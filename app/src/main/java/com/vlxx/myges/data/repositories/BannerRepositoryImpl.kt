package com.vlxx.myges.data.repositories

import com.vlxx.myges.data.dtos.BannerDto
import com.vlxx.myges.data.network.Api
import com.vlxx.myges.domain.repositories.BannerRepository
import timber.log.Timber

class BannerRepositoryImpl(
    private val api: Api
) : BannerRepository {
    override suspend fun getBanners(): List<BannerDto> {
        Timber.d("Fetching banners...")
        val response = api.getBanners()
        Timber.d("Banners API Response: $response")
        Timber.d("Number of banners: ${response.result.content.size}")
        response.result.content.forEachIndexed { index, banner ->
            Timber.d("Banner #$index: $banner")
        }
        return response.result.content
    }
}
