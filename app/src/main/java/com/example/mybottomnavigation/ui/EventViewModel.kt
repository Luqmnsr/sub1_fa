package com.example.mybottomnavigation.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybottomnavigation.data.response.DetailResponse
import com.example.mybottomnavigation.data.response.Event
import com.example.mybottomnavigation.data.response.EventResponse
import com.example.mybottomnavigation.data.response.ListEventsItem
import com.example.mybottomnavigation.data.retrofit.ApiConfig
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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchFinishedEventData()
        fetchUpcomingEvents()
    }

    // Load finished events when the fragment is initialized
    fun loadFinishedEvents() {
        _isLoading.value = true
        ApiConfig.getApiService().getFinishedEvents().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listEvent.value = response.body()?.listEvents ?: emptyList()
                    isFinishedEventsLoaded = true
                } else {
                    _errorMessage.value = "Failed to load finished events"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                if (!isFinishedEventsLoaded) {  // Only show error if finished events not loaded
                    _errorMessage.value = "Error: ${t.message}"
                }
            }
        })
    }

    // Search finished events based on query
    fun searchFinishedEvents(keyword: String) {
        _isLoading.value = true
        ApiConfig.getApiService().searchEvents(active = 0, keyword = keyword).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listEvent.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to search events"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

    // Fetching unfinished events
    fun fetchUpcomingEvents() {
        if (!isDataLoaded) {
            _isLoading.value = true
            ApiConfig.getApiService().getUpcomingEvents().enqueue(object : Callback<EventResponse> {
                override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _upcomingEvents.value = response.body()?.listEvents ?: emptyList()
                        isDataLoaded = true
                    } else {
                        _errorMessage.value = "Failed to load unfinished events"
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    _isLoading.value = false
                    if (!isDataLoaded) {  // Only show error if unfinished events not loaded
                        _errorMessage.value = "Error fetching unfinished events: ${t.message}"
                    }
                }
            })
        }
    }

    // Fetching finished events
    private fun fetchFinishedEventData() {
        _isLoading.value = true  // Tampilkan progress bar
        ApiConfig.getApiService().getFinishedEvents().enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    _listEvent.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load finished events: ${response.message()}"
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _errorMessage.value = "Error fetching unfinished events: ${t.message}"
                _isLoading.value = false
            }
        })
    }

    // Fetching event details
    fun fetchDetailEvent(eventId: String) {
        ApiConfig.getApiService().getDetailEvent(eventId).enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                if (response.isSuccessful) {
                    val detailResponse = response.body()
                    if (detailResponse != null && detailResponse.error != true) {
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