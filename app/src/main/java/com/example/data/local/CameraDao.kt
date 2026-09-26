package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Camera
import kotlinx.coroutines.flow.Flow

@Dao
interface CameraDao {
    @Query("SELECT * FROM cameras ORDER BY id ASC")
    fun getAllCameras(): Flow<List<Camera>>

    @Query("SELECT * FROM cameras WHERE id = :id LIMIT 1")
    suspend fun getCameraById(id: Long): Camera?

    @Query("SELECT * FROM cameras WHERE isArmed = 1")
    fun getArmedCameras(): Flow<List<Camera>>

    @Query("SELECT COUNT(*) FROM cameras")
    fun getCameraCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCamera(camera: Camera): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCameras(cameras: List<Camera>)

    @Update
    suspend fun updateCamera(camera: Camera)

    @Delete
    suspend fun deleteCamera(camera: Camera)

    @Query("DELETE FROM cameras WHERE id = :id")
    suspend fun deleteCameraById(id: Long)

    @Query("UPDATE cameras SET isArmed = :isArmed")
    suspend fun setAllArmed(isArmed: Boolean)

    @Query("UPDATE cameras SET isArmed = :isArmed WHERE id = :id")
    suspend fun setCameraArmed(id: Long, isArmed: Boolean)
}
