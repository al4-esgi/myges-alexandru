package com.vlxx.myges.data.repositories

import com.vlxx.myges.data.dtos.AgendaEventDto
import com.vlxx.myges.data.network.Api
import com.vlxx.myges.domain.repositories.AgendaRepository
import timber.log.Timber

class AgendaRepositoryImpl(
    private val api: Api
) : AgendaRepository {
    override suspend fun getAgenda(start: Long, end: Long): List<AgendaEventDto> {
        Timber.d("Fetching agenda with start=$start, end=$end")
        val response = api.getAgenda(start, end)
        Timber.d("Agenda API Response: $response")
        Timber.d("Number of events: ${response.result.size}")
        response.result.forEachIndexed { index, event ->
            Timber.d("Event #$index: $event")
        }
        return response.result
    }
}
