package com.example.mybottomnavigation.ui.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybottomnavigation.data.EventRepository
import com.example.mybottomnavigation.data.local.entity.EventEntity
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: EventRepository) : ViewModel() {

    // LiveData untuk daftar event favorit
    private val _favoriteEvents = MediatorLiveData<List<EventEntity>>()
    val favoriteEvents: LiveData<List<EventEntity>> get() = _favoriteEvents

    // LiveData untuk indikator loading
    private val _isFavoriteLoading = MutableLiveData<Boolean>()
    val isFavoriteLoading: LiveData<Boolean> get() = _isFavoriteLoading

    // LiveData untuk pesan error
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    init {
        fetchFavorites()
    }

    private fun fetchFavorites() {
        _isFavoriteLoading.value = true
        Log.d("FavoriteViewModel", "Start loading favorites")
        viewModelScope.launch {
            try {
                val liveData = repository.getAllFavoriteEvents()
                _favoriteEvents.addSource(liveData) { data ->
                    _favoriteEvents.value = data
                    _isFavoriteLoading.value = false

                    if (data.isEmpty()) {
                        _favoriteEvents.value = emptyList() // Ensure LiveData is set to an empty list
                        Log.d("FavoriteViewModel", "Favorites list is empty")
                    }
                    Log.d("FavoriteViewModel", "Favorites loaded")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isFavoriteLoading.value = false
                Log.d("FavoriteViewModel", "Error loading favorites: ${e.message}")
            }
        }
    }

}