package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_events")
data class SecurityEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cameraId: Long,
    val cameraName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: EventType = EventType.PERSON_DETECTED,
    val personName: String? = null, // e.g. "Unknown Person" or "David (Family)"
    val confidence: Float = 0.94f,
    val thumbnailUrl: String = "",
    val clipUrl: String = "",
    val clipDurationSeconds: Int = 20, // 5s pre-event + 15s post-event
    val isRead: Boolean = false,
    val isBookmarked: Boolean = false,
    val fileSizeBytes: Long = 4500000L, // ~4.5 MB
    val details: String = ""
)

enum class EventType(val displayName: String, val badgeColorHex: Long) {
    PERSON_DETECTED("Person Detected", 0xFF00D2FF),
    UNKNOWN_PERSON("Unknown Person", 0xFFF43F5E),
    FACE_RECOGNIZED("Known Person", 0xFF10B981),
    MOTION_DETECTED("Motion Alert", 0xFFF59E0B),
    ZONE_INTRUSION("Perimeter Intrusion", 0xFFEC4899)
}
