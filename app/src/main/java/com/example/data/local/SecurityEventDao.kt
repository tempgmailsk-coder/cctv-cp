package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SecurityEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityEventDao {
    @Query("SELECT * FROM security_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<SecurityEvent>>

    @Query("SELECT * FROM security_events WHERE id = :id LIMIT 1")
    suspend fun getEventById(id: Long): SecurityEvent?

    @Query("SELECT * FROM security_events WHERE cameraId = :cameraId ORDER BY timestamp DESC")
    fun getEventsByCamera(cameraId: Long): Flow<List<SecurityEvent>>

    @Query("SELECT * FROM security_events WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedEvents(): Flow<List<SecurityEvent>>

    @Query("SELECT * FROM security_events ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEvents(limit: Int = 5): Flow<List<SecurityEvent>>

    @Query("SELECT COUNT(*) FROM security_events WHERE timestamp >= :sinceTimestamp")
    fun getEventCountSince(sinceTimestamp: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: SecurityEvent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<SecurityEvent>)

    @Update
    suspend fun updateEvent(event: SecurityEvent)

    @Delete
    suspend fun deleteEvent(event: SecurityEvent)

    @Query("DELETE FROM security_events WHERE id = :id")
    suspend fun deleteEventById(id: Long)

    @Query("DELETE FROM security_events WHERE timestamp < :cutoffTimestamp AND isBookmarked = 0")
    suspend fun deleteOldEvents(cutoffTimestamp: Long): Int

    @Query("DELETE FROM security_events")
    suspend fun clearAllEvents()
}
