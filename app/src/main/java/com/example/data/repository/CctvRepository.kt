package com.example.data.repository

import android.content.Context
import com.example.data.cpplus.CpPlusClient
import com.example.data.local.AppDatabase
import com.example.data.local.SurveillancePreferences
import com.example.data.model.Camera
import com.example.data.model.EventType
import com.example.data.model.KnownPerson
import com.example.data.model.SecurityEvent
import com.example.service.CctvSurveillanceService
import com.example.service.NotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.io.File

class CctvRepository(
    private val context: Context,
    private val database: AppDatabase = AppDatabase.getInstance(context),
    val preferences: SurveillancePreferences = SurveillancePreferences(context),
    val cpPlusClient: CpPlusClient = CpPlusClient()
) {
    private val notificationHelper = NotificationHelper(context)

    // Camera queries
    val allCameras: Flow<List<Camera>> = database.cameraDao().getAllCameras()
    val armedCameras: Flow<List<Camera>> = database.cameraDao().getArmedCameras()
    val cameraCount: Flow<Int> = database.cameraDao().getCameraCount()

    suspend fun getCameraById(id: Long): Camera? = database.cameraDao().getCameraById(id)
    suspend fun saveCamera(camera: Camera): Long = database.cameraDao().insertCamera(camera)
    suspend fun updateCamera(camera: Camera) = database.cameraDao().updateCamera(camera)
    suspend fun deleteCamera(camera: Camera) = database.cameraDao().deleteCamera(camera)
    suspend fun deleteCameraById(id: Long) = database.cameraDao().deleteCameraById(id)

    suspend fun toggleCameraArmed(id: Long, armed: Boolean) {
        database.cameraDao().setCameraArmed(id, armed)
    }

    suspend fun setAllArmed(isArmed: Boolean) {
        preferences.isSystemArmed = isArmed
        database.cameraDao().setAllArmed(isArmed)
        if (isArmed) {
            CctvSurveillanceService.startService(context)
        }
    }

    // Security Events queries
    val allEvents: Flow<List<SecurityEvent>> = database.securityEventDao().getAllEvents()
    val bookmarkedEvents: Flow<List<SecurityEvent>> = database.securityEventDao().getBookmarkedEvents()
    val recentEvents: Flow<List<SecurityEvent>> = database.securityEventDao().getRecentEvents(10)

    fun getEventCountSince(sinceTimestamp: Long): Flow<Int> =
        database.securityEventDao().getEventCountSince(sinceTimestamp)

    suspend fun getEventById(id: Long): SecurityEvent? = database.securityEventDao().getEventById(id)
    suspend fun updateEvent(event: SecurityEvent) = database.securityEventDao().updateEvent(event)
    suspend fun deleteEvent(event: SecurityEvent) = database.securityEventDao().deleteEvent(event)
    suspend fun deleteEventById(id: Long) = database.securityEventDao().deleteEventById(id)
    suspend fun clearAllEvents() = database.securityEventDao().clearAllEvents()

    suspend fun toggleBookmark(event: SecurityEvent) {
        database.securityEventDao().updateEvent(event.copy(isBookmarked = !event.isBookmarked))
    }

    // Known Persons queries
    val allKnownPersons: Flow<List<KnownPerson>> = database.knownPersonDao().getAllPersons()
    suspend fun saveKnownPerson(person: KnownPerson): Long = database.knownPersonDao().insertPerson(person)
    suspend fun deleteKnownPerson(person: KnownPerson) = database.knownPersonDao().deletePerson(person)

    // PTZ & Camera hardware tests
    suspend fun testCameraConnection(camera: Camera): CpPlusClient.ConnectionResult {
        return cpPlusClient.testConnection(camera)
    }

    suspend fun sendPtz(camera: Camera, direction: String): Boolean {
        return cpPlusClient.sendPtzCommand(camera, direction)
    }

    // Trigger test detection (e.g. from UI testing or background alert)
    suspend fun triggerDetectionEvent(cameraId: Long, eventType: EventType): SecurityEvent {
        val camera = database.cameraDao().getCameraById(cameraId)
            ?: database.cameraDao().getAllCameras().firstOrNull()?.firstOrNull()
            ?: Camera(name = "CP PLUS Front Gate", ipAddress = "192.168.1.108")

        val knownPersons = database.knownPersonDao().getAllPersons().firstOrNull() ?: emptyList()
        val personName = when (eventType) {
            EventType.FACE_RECOGNIZED -> knownPersons.firstOrNull()?.name ?: "Elena Vance"
            EventType.UNKNOWN_PERSON -> "Unrecognized Subject"
            EventType.PERSON_DETECTED -> "Pedestrian / Visitor"
            EventType.ZONE_INTRUSION -> "Zone Boundary Intrusion"
            EventType.MOTION_DETECTED -> "General Motion"
        }

        val event = SecurityEvent(
            cameraId = camera.id,
            cameraName = camera.name,
            timestamp = System.currentTimeMillis(),
            eventType = eventType,
            personName = personName,
            confidence = if (eventType == EventType.UNKNOWN_PERSON) 0.96f else 0.91f,
            clipDurationSeconds = preferences.preBufferSeconds + preferences.postBufferSeconds,
            details = "CP PLUS Smart Motion Detection: Auto-buffered clip (5s pre + 15s post-event). Environmental interference suppressed."
        )

        val id = database.securityEventDao().insertEvent(event)
        val saved = event.copy(id = id)

        if (preferences.notificationsEnabled) {
            notificationHelper.showSecurityEventNotification(saved)
        }

        return saved
    }

    // Clean up expired clips based on retention days
    suspend fun cleanupOldEvents(): Int {
        val retentionMillis = preferences.retentionDays * 24L * 60L * 60L * 1000L
        val cutoff = System.currentTimeMillis() - retentionMillis
        return database.securityEventDao().deleteOldEvents(cutoff)
    }
}
