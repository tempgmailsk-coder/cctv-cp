package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.EventType
import com.example.data.model.SecurityEvent

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_SURVEILLANCE_SERVICE = "cctv_surveillance_service_channel"
        const val CHANNEL_ALERT_ID = "cctv_alerts_channel"
        const val NOTIFICATION_ID_SERVICE = 1001
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Foreground monitoring service channel (Low priority, persistent)
            val serviceChannel = NotificationChannel(
                CHANNEL_SURVEILLANCE_SERVICE,
                "Surveillance Background Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors CP PLUS DVR/NVR event stream for human and motion alerts"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(serviceChannel)

            // High priority alerts channel (Heads-up, vibration, alarm)
            val alertChannel = NotificationChannel(
                CHANNEL_ALERT_ID,
                "Intrusion & Person Detection Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Instant notifications when human or intrusion motion is detected on cameras"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(alertChannel)
        }
    }

    fun showSecurityEventNotification(event: SecurityEvent) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_EVENT_ID", event.id)
            putExtra("NAVIGATE_TO", "event_player")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            event.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (event.eventType) {
            EventType.UNKNOWN_PERSON -> "⚠️ Unknown person detected at ${event.cameraName}"
            EventType.PERSON_DETECTED -> "👤 Person detected at ${event.cameraName}"
            EventType.FACE_RECOGNIZED -> "✅ Known Person (${event.personName}) at ${event.cameraName}"
            EventType.ZONE_INTRUSION -> "🚨 Perimeter Intrusion at ${event.cameraName}"
            EventType.MOTION_DETECTED -> "🏃 Motion detected at ${event.cameraName}"
        }

        val content = if (event.personName != null && event.eventType != EventType.UNKNOWN_PERSON) {
            "Identified: ${event.personName} (${(event.confidence * 100).toInt()}% match) • Clip recorded"
        } else {
            "Smart Motion Alert • 20s clip recorded automatically"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERT_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$content\nTimestamp: ${java.text.SimpleDateFormat("hh:mm:ss a, dd MMM", java.util.Locale.getDefault()).format(event.timestamp)}\nCamera: ${event.cameraName}\nTap to watch recorded video clip.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(event.id.toInt(), notification)
    }
}
