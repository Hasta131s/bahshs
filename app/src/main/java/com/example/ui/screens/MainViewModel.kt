package com.example.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
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

    val allShows = allMedia.map { mediaList ->
        mediaList.distinctBy { it.showName }.map { ShowInfo(it.showName, it.logoUrl, it.category) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val showDetailsMap = dao.getAllShowDetailsFlow().map { list ->
        list.associateBy { it.showName }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())
    
    val searchResults = combine(allShows, _searchQuery) { shows, query ->
        if (query.isEmpty()) emptyList() else shows.filter { it.showName.contains(query, true) || it.category.contains(query, true) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedShowName = MutableStateFlow("")
    val selectedShowEpisodes = combine(allMedia, _selectedShowName) { media, showName ->
        media.filter { it.showName == showName }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            try {
                if (dao.getAll().isEmpty()) {
                    val parsed = M3uParser.parseFromAssets(context.applicationContext)
                    dao.insertAll(parsed)
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error loading media list from assets", e)
            }
            
            // Collect shows and pre-fetch details safely and sequentially to prevent spamming DB/network
            launch {
                try {
                    allShows.collectLatest { shows ->
                        shows.forEach { show ->
                            try {
                                val cached = dao.getShowDetails(show.showName)
                                if (cached == null) {
                                    val fetched = OmdbHelper.fetchShowDetails(show.showName)
                                    if (fetched != null) {
                                        dao.insertShowDetails(fetched)
                                    }
                                    kotlinx.coroutines.delay(200) // 200ms delay to keep CPU, network and database fully responsive
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("MainViewModel", "Error fetching details for ${show.showName}", e)
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("MainViewModel", "Error in shows flow collector", e)
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectShow(name: String) {
        _selectedShowName.value = name
        viewModelScope.launch {
            try {
                val cached = dao.getShowDetails(name)
                if (cached == null || cached.posterUrl.isEmpty()) {
                    val fetched = OmdbHelper.fetchShowDetails(name)
                    if (fetched != null) {
                        dao.insertShowDetails(fetched)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error fetching detailed show info for $name", e)
            }
        }
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
