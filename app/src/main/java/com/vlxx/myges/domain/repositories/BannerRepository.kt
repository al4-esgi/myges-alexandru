package com.vlxx.myges.domain.repositories

import com.vlxx.myges.data.dtos.BannerDto

interface BannerRepository {
    suspend fun getBanners(): List<BannerDto>
}
