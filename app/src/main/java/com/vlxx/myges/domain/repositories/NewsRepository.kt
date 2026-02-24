package com.vlxx.myges.domain.repositories

import com.vlxx.myges.data.dtos.NewsDto

interface NewsRepository {
    suspend fun getNews(): List<NewsDto>
}
