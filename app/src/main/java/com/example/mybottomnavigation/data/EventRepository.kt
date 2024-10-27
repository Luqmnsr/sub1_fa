package com.example.mybottomnavigation.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.mybottomnavigation.data.local.entity.EventEntity
import com.example.mybottomnavigation.data.local.room.EventDao
import com.example.mybottomnavigation.data.local.room.EventRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class EventRepository(application: Application) {
    private val eventDao: EventDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = EventRoomDatabase.getDatabase(application)
        eventDao = db.eventDao()
    }

    fun getAllFavoriteEvents(): LiveData<List<EventEntity>> {
        return eventDao.getAllFavoriteEvents()
    }

    fun getFavoriteEventById(id: String): LiveData<EventEntity> {
        return eventDao.getFavoriteEventById(id)
    }

    fun insertFavorite(eventEntity: EventEntity) {
        executorService.execute { eventDao.insertFavorite(eventEntity) }
    }

    fun deleteFavorite(eventEntity: EventEntity) {
        executorService.execute { eventDao.deleteFavorite(eventEntity) }
    }
}