package com.example.mybottomnavigation.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mybottomnavigation.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavorite(eventEntity: EventEntity)

    @Delete
    fun deleteFavorite(eventEntity: EventEntity)

    @Query("SELECT * FROM EventEntity WHERE id = :id")
    fun getFavoriteEventById(id: String): LiveData<EventEntity>

    @Query("SELECT * FROM EventEntity")
    fun getAllFavoriteEvents(): LiveData<List<EventEntity>>
}