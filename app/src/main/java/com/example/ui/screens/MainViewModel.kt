package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MediaEntity
import com.example.data.MediaRepository
import com.example.data.OmdbResponse
import com.example.data.ShowInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MediaRepository) : ViewModel() {

    val allShows: StateFlow<List<ShowInfo>> = repository.getAllShows()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favorites: StateFlow<List<MediaEntity>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val history: StateFlow<List<MediaEntity>> = repository.getWatchHistory()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
    val downloads: StateFlow<List<MediaEntity>> = repository.getDownloads()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ShowInfo>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        val all = allShows.value
        _searchResults.value = if (query.isBlank()) {
            emptyList()
        } else {
            all.filter { it.showName.contains(query, ignoreCase = true) }
        }
    }

    private val _selectedShowDetails = MutableStateFlow<OmdbResponse?>(null)
    val selectedShowDetails = _selectedShowDetails.asStateFlow()
    
    private val _selectedShowEpisodes = MutableStateFlow<List<MediaEntity>>(emptyList())
    val selectedShowEpisodes = _selectedShowEpisodes.asStateFlow()

    fun loadShowDetails(showName: String) {
        viewModelScope.launch {
            _selectedShowDetails.value = null
            _selectedShowDetails.value = repository.getShowDetails(showName)
            
            repository.getMediaByShow(showName).collect { episodes ->
                _selectedShowEpisodes.value = episodes
            }
        }
    }

    fun toggleFavorite(id: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.setFavorite(id, !currentStatus)
        }
    }
}
