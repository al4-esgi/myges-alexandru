package com.vlxx.myges.ui.screens.authenticated.grades.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlxx.myges.data.dtos.CourseDto
import com.vlxx.myges.domain.repositories.GradesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class GradesUiState {
    object Loading : GradesUiState()
    data class Success(
        val allCourses: List<CourseDto>,
        val filteredCourses: List<CourseDto>,
        val selectedYear: Int,
        val selectedTrimester: String?,
        val availableTrimesters: List<String>
    ) : GradesUiState()
    data class Error(val message: String) : GradesUiState()
}

class GradesViewModel(
    private val gradesRepository: GradesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GradesUiState>(GradesUiState.Loading)
    val uiState: StateFlow<GradesUiState> = _uiState.asStateFlow()

    init {
        loadGrades(2025)
    }

    fun loadGrades(year: Int) {
        _uiState.value = GradesUiState.Loading
        Timber.d("Loading grades for year $year...")
        viewModelScope.launch {
            try {
                val courses = gradesRepository.getGrades(year)
                Timber.d("Grades loaded successfully: ${courses.size} courses")

                val trimesters = courses
                    .mapNotNull { it.trimesterName }
                    .distinct()
                    .sorted()

                _uiState.value = GradesUiState.Success(
                    allCourses = courses,
                    filteredCourses = courses,
                    selectedYear = year,
                    selectedTrimester = null,
                    availableTrimesters = trimesters
                )
            } catch (e: Exception) {
                Timber.e(e, "Error loading grades - Message: ${e.message}")
                e.printStackTrace()
                _uiState.value = GradesUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun filterByTrimester(trimester: String?) {
        val currentState = _uiState.value
        if (currentState is GradesUiState.Success) {
            val filtered = if (trimester == null) {
                currentState.allCourses
            } else {
                currentState.allCourses.filter { it.trimesterName == trimester }
            }

            _uiState.value = currentState.copy(
                filteredCourses = filtered,
                selectedTrimester = trimester
            )
        }
    }
}
