package com.vlxx.myges.domain.repositories

import com.vlxx.myges.data.dtos.AgendaEventDto

interface AgendaRepository {
    suspend fun getAgenda(start: Long, end: Long): List<AgendaEventDto>
}
