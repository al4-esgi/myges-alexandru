package com.vlxx.myges.ui.screens.authenticated.agenda.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlxx.myges.data.dtos.AgendaEventDto
import com.vlxx.myges.domain.repositories.AgendaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

sealed class AgendaUiState {
    object Loading : AgendaUiState()
    data class Success(val events: List<AgendaEventDto>) : AgendaUiState()
    data class Error(val message: String) : AgendaUiState()
}

class AgendaViewModel(
    private val agendaRepository: AgendaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AgendaUiState>(AgendaUiState.Loading)
    val uiState: StateFlow<AgendaUiState> = _uiState.asStateFlow()

    init {
        loadAgenda()
    }

    fun loadAgenda() {
        _uiState.value = AgendaUiState.Loading
        Timber.d("Loading agenda...")
        viewModelScope.launch {
            try {
                val start = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val end = Calendar.getInstance().apply {
                    add(Calendar.MONTH, 2)
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val startTimestamp = start.timeInMillis
                val endTimestamp = end.timeInMillis

                Timber.d("Fetching agenda from $startTimestamp to $endTimestamp")
                val events = agendaRepository.getAgenda(startTimestamp, endTimestamp)
                Timber.d("Agenda loaded successfully: ${events.size} events")
                _uiState.value = AgendaUiState.Success(events)
            } catch (e: Exception) {
                Timber.e(e, "Error loading agenda - Message: ${e.message}")
                e.printStackTrace()
                _uiState.value = AgendaUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
