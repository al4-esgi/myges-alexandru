package com.vlxx.myges.domain.repositories

import com.vlxx.myges.data.dtos.CourseDto

interface GradesRepository {
    suspend fun getGrades(year: Int): List<CourseDto>
}
