package com.example.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppContainer
import com.example.data.M3uParser
import com.example.data.MediaEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ShowInfo(val showName: String, val logoUrl: String, val category: String)

class MainViewModel(private val appContainer: AppContainer, private val context: Context) : ViewModel() {
    private val dao = appContainer.database.mediaDao()
    
    val allMedia = dao.getAllFlow().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val favorites = dao.getFavorites().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val history = dao.getHistory().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    
    val searchResults = combine(allMedia, _searchQuery) { media, query ->
        if (query.isEmpty()) media else media.filter { it.showName.contains(query, true) || it.title.contains(query, true) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allShows = allMedia.map { mediaList ->
        mediaList.distinctBy { it.showName }.map { ShowInfo(it.showName, it.logoUrl, it.category) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedShowName = MutableStateFlow("")
    val selectedShowEpisodes = combine(allMedia, _selectedShowName) { media, showName ->
        media.filter { it.showName == showName }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            if (dao.getAll().isEmpty()) {
                val parsed = M3uParser.parseFromAssets(context)
                dao.insertAll(parsed)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectShow(name: String) {
        _selectedShowName.value = name
    }

    fun toggleFavorite(id: String, current: Boolean) {
        viewModelScope.launch {
            dao.getById(id)?.let {
                it.isFavorite = !current
                dao.update(it)
            }
        }
    }
}
