package com.vlxx.myges.data.repositories

import com.vlxx.myges.data.dtos.CourseDto
import com.vlxx.myges.data.network.Api
import com.vlxx.myges.domain.repositories.GradesRepository
import timber.log.Timber

class GradesRepositoryImpl(
    private val api: Api
) : GradesRepository {
    override suspend fun getGrades(year: Int): List<CourseDto> {
        Timber.d("========================================")
        Timber.d("Fetching grades for year $year...")
        Timber.d("API endpoint: me/$year/grades")

        try {
            val response = api.getGrades(year)
            Timber.d("========================================")
            Timber.d("✅ Grades API Response received successfully")
            Timber.d("Number of courses: ${response.result.size}")

            response.result.forEachIndexed { index, course ->
                Timber.d("----------------------------------------")
                Timber.d("Course #$index:")
                Timber.d("  Name: ${course.course}")
                Timber.d("  Teacher: ${course.teacherCivility} ${course.teacherFirstName} ${course.teacherLastName}")
                Timber.d("  Trimester: ${course.trimesterName}")
                Timber.d("  Year: ${course.year}")
                Timber.d("  Average: ${course.average}")
                Timber.d("  CC Average: ${course.ccAverage}")
                Timber.d("  Exam: ${course.exam}")
                Timber.d("  Letter Mark: ${course.letterMark}")
                Timber.d("  ECTS: ${course.ects}")
                Timber.d("  Coef: ${course.coef}")
                Timber.d("  Grades: ${course.grades}")
            }
            Timber.d("========================================")

            return response.result
        } catch (e: Exception) {
            Timber.e("========================================")
            Timber.e("❌ Error fetching grades for year $year")
            Timber.e("Error type: ${e::class.simpleName}")
            Timber.e("Error message: ${e.message}")
            Timber.e("Stack trace:")
            e.printStackTrace()
            Timber.e("========================================")
            throw e
        }
    }
}
