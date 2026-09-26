package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cameras")
data class Camera(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val ipAddress: String,
    val rtspPort: Int = 554,
    val httpPort: Int = 80,
    val channel: Int = 1,
    val username: String = "admin",
    val passwordEncrypted: String = "",
    val modelPreset: String = "CP_PLUS_ORANGE", // CP_PLUS_ORANGE, CP_PLUS_COSMIC, CP_PLUS_INDIGO, CP_PLUS_EZYKAM, ONVIF_GENERIC
    val streamType: String = "SUB", // MAIN (HD) or SUB (Smooth)
    val isOnline: Boolean = true,
    val isArmed: Boolean = true,
    val motionEnabled: Boolean = true,
    val personOnlyFilter: Boolean = true, // Smart filter for human bodies
    val sensitivity: Int = 75, // 1 to 100
    val detectionZonesGrid: String = "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15", // 4x4 grid of zones active
    val lastSeenTimestamp: Long = System.currentTimeMillis()
) {
    /**
     * Builds standard CP PLUS / Dahua RTSP URL
     * Syntax: rtsp://username:password@ip:port/cam/realmonitor?channel=X&subtype=Y
     * subtype=0 for main stream, subtype=1 for sub stream
     */
    fun buildRtspUrl(passwordDecrypted: String = passwordEncrypted): String {
        val subtype = if (streamType.equals("MAIN", ignoreCase = true)) 0 else 1
        return if (passwordDecrypted.isNotEmpty()) {
            "rtsp://$username:$passwordDecrypted@$ipAddress:$rtspPort/cam/realmonitor?channel=$channel&subtype=$subtype"
        } else {
            "rtsp://$username@$ipAddress:$rtspPort/cam/realmonitor?channel=$channel&subtype=$subtype"
        }
    }

    /**
     * Builds CP PLUS HTTP CGI snapshot endpoint
     */
    fun buildSnapshotUrl(): String {
        return "http://$ipAddress:$httpPort/cgi-bin/snapshot.cgi?channel=$channel"
    }

    /**
     * Builds CP PLUS HTTP CGI event stream endpoint for IVS / SMD events
     */
    fun buildEventStreamUrl(): String {
        return "http://$ipAddress:$httpPort/cgi-bin/eventManager.cgi?action=attach&codes=[CrossLineDetection,VideoMotion,CrossRegionDetection,FaceDetection,SmartMotionHuman]"
    }
}
