package com.vlxx.myges.data.repositories

import com.vlxx.myges.data.dtos.NewsDto
import com.vlxx.myges.data.network.Api
import com.vlxx.myges.domain.repositories.NewsRepository
import timber.log.Timber

class NewsRepositoryImpl(
    private val api: Api
) : NewsRepository {
    override suspend fun getNews(): List<NewsDto> {
        Timber.d("Fetching news...")
        val response = api.getNews()
        Timber.d("News API Response: $response")
        Timber.d("Number of news: ${response.result.content.size}")
        return response.result.content
    }
}
