package com.example.mybottomnavigation.data.retrofit

import com.example.mybottomnavigation.data.response.DetailResponse
import com.example.mybottomnavigation.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Get Finished Events (active=0)
    @GET("events")
    fun getFinishedEvents(
        @Query("active") active: Int = 0
    ): Call<EventResponse>

    // Get Upcoming Events (active=1)
    @GET("events")
    fun getUpcomingEvents(
        @Query("active") active: Int = 1
    ): Call<EventResponse>

    // Get Detail Event by ID
    @GET("events/{id}")
    fun getDetailEvent(
        @Path("id") id: String
    ): Call<DetailResponse>

    // Search Event by keyword (active=-1)
    @GET("events")
    fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") keyword: String
    ): Call<EventResponse>
}