package com.vlxx.myges.ui.screens.authenticated.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlxx.myges.data.dtos.BannerDto
import com.vlxx.myges.data.dtos.NewsDto
import com.vlxx.myges.domain.repositories.BannerRepository
import com.vlxx.myges.domain.repositories.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val banners: List<BannerDto>, val news: List<NewsDto>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val bannerRepository: BannerRepository,
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadContent()
    }

    fun loadContent() {
        _uiState.value = HomeUiState.Loading
        Timber.d("Loading banners and news...")
        viewModelScope.launch {
            try {
                val banners = bannerRepository.getBanners()
                val news = newsRepository.getNews()
                Timber.d("Content loaded successfully: ${banners.size} banners, ${news.size} news")
                _uiState.value = HomeUiState.Success(banners, news)
            } catch (e: Exception) {
                Timber.e(e, "Error loading content - Message: ${e.message}")
                e.printStackTrace()
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
