package com.example.mybottomnavigation.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybottomnavigation.data.remote.response.DetailResponse
import com.example.mybottomnavigation.data.remote.response.Event
import com.example.mybottomnavigation.data.remote.response.EventResponse
import com.example.mybottomnavigation.data.remote.response.ListEventsItem
import com.example.mybottomnavigation.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventViewModel : ViewModel() {

    private var isDataLoaded = false
    var isFinishedEventsLoaded = false

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> get() = _upcomingEvents

    private val _listEvent = MutableLiveData<List<ListEventsItem>>()
    val listEvent: LiveData<List<ListEventsItem>> get() = _listEvent

    private val _eventDetail = MutableLiveData<Event?>()
    val eventDetail: LiveData<Event?> get() = _eventDetail

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isFinishedLoading = MutableLiveData<Boolean>()
    val isFinishedLoading: LiveData<Boolean> get() = _isFinishedLoading

    private val _isUpcomingLoading = MutableLiveData<Boolean>()
    val isUpcomingLoading: LiveData<Boolean> get() = _isUpcomingLoading

    init {
        fetchFinishedEvents()
        fetchUpcomingEvents()
    }

    // Load finished events when the fragment is initialized
    fun loadFinishedEvents() {
        _isFinishedLoading.value = true
        ApiConfig.getApiService().getFinishedEvents().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isFinishedLoading.value = false
                if (response.isSuccessful) {
                    _listEvent.value = response.body()?.listEvents ?: emptyList()
                    isFinishedEventsLoaded = true
                } else {
                    _errorMessage.value = "Failed to load finished events"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isFinishedLoading.value = false
                if (!isFinishedEventsLoaded) {  // Only show error if finished events not loaded
                    _errorMessage.value = "Error: ${t.message}"
                }
            }
        })
    }

    // Search finished events based on query
    fun searchFinishedEvents(keyword: String) {
        _isFinishedLoading.value = true
        ApiConfig.getApiService().searchEvents(active = 0, keyword = keyword).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isFinishedLoading.value = false
                if (response.isSuccessful) {
                    _listEvent.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to search events"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isFinishedLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

    // Fetching upcoming events
    fun fetchUpcomingEvents() {
        if (!isDataLoaded) {
            _isUpcomingLoading.value = true
            ApiConfig.getApiService().getUpcomingEvents().enqueue(object : Callback<EventResponse> {
                override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                    _isUpcomingLoading.value = false
                    if (response.isSuccessful) {
                        _upcomingEvents.value = response.body()?.listEvents ?: emptyList()
                        isDataLoaded = true
                    } else {
                        _errorMessage.value = "Failed to load upcoming events"
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    _isUpcomingLoading.value = false
                    if (!isDataLoaded) {  // Only show error if upcoming events not loaded
                        _errorMessage.value = "Error fetching upcoming events: ${t.message}"
                    }
                }
            })
        }
    }

    // Fetching finished events
    private fun fetchFinishedEvents() {
        _isFinishedLoading.value = true  // Tampilkan progress bar
        ApiConfig.getApiService().getFinishedEvents().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    _listEvent.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load finished events: ${response.message()}"
                }
                _isFinishedLoading.value = false
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _errorMessage.value = "Error fetching finished events: ${t.message}"
                _isFinishedLoading.value = false
            }
        })
    }

    // Fetching event details
    fun fetchDetailEvent(eventId: String) {
        ApiConfig.getApiService().getDetailEvent(eventId).enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                if (response.isSuccessful) {
                    val detailResponse = response.body()
                    if (detailResponse?.error == false) {
                        _eventDetail.value = detailResponse.event
                    } else {
                        _errorMessage.value = detailResponse?.message ?: "Event not found"
                    }
                } else {
                    _errorMessage.value = "Failed to load event details: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _errorMessage.value = "Error fetching event details: ${t.message}"
            }
        })
    }

}