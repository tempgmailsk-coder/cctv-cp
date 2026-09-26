package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.SurveillancePreferences
import com.example.data.model.EventType
import com.example.data.model.SecurityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

class CctvSurveillanceService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var preferences: SurveillancePreferences
    private lateinit var database: AppDatabase

    companion object {
        const val ACTION_START = "ACTION_START_SURVEILLANCE"
        const val ACTION_STOP = "ACTION_STOP_SURVEILLANCE"
        const val ACTION_SIMULATE_TRIGGER = "ACTION_SIMULATE_TRIGGER"

        fun startService(context: Context) {
            val intent = Intent(context, CctvSurveillanceService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, CctvSurveillanceService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        preferences = SurveillancePreferences(this)
        database = AppDatabase.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_SIMULATE_TRIGGER -> {
                val cameraId = intent.getLongExtra("CAMERA_ID", 1L)
                val typeName = intent.getStringExtra("EVENT_TYPE") ?: EventType.PERSON_DETECTED.name
                val eventType = try { EventType.valueOf(typeName) } catch (e: Exception) { EventType.PERSON_DETECTED }
                serviceScope.launch {
                    processDetectedEvent(cameraId, eventType)
                }
            }
            else -> {
                startForegroundNotification()
                startEventMonitoringLoop()
            }
        }
        return START_STICKY
    }

    private fun startForegroundNotification() {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_SURVEILLANCE_SERVICE)
            .setContentTitle("CP Guard Sentinel Active")
            .setContentText("Continuous surveillance active on CP PLUS DVR/NVR cameras")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NotificationHelper.NOTIFICATION_ID_SERVICE,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NotificationHelper.NOTIFICATION_ID_SERVICE, notification)
        }
    }

    private fun startEventMonitoringLoop() {
        serviceScope.launch {
            while (isActive) {
                if (preferences.isSystemArmed) {
                    // Check connected cameras
                    val cameras = database.cameraDao().getAllCameras().firstOrNull() ?: emptyList()
                    val armedCameras = cameras.filter { it.isArmed && it.isOnline }

                    // Real CP PLUS implementation connects to CGI stream:
                    // http://<ip>:<port>/cgi-bin/eventManager.cgi?action=attach&codes=[CrossLineDetection,VideoMotion,FaceDetection,SmartMotionHuman]
                    // If hardware response or background alert is received:
                    // Here we maintain socket/HTTP keepalive.
                }
                delay(10000) // 10s health check pulse
            }
        }
    }

    suspend fun processDetectedEvent(cameraId: Long, eventType: EventType) {
        val camera = database.cameraDao().getCameraById(cameraId) ?: return
        if (!camera.isArmed) return

        val knownPersons = database.knownPersonDao().getAllPersons().firstOrNull() ?: emptyList()
        val matchedPerson = if (eventType == EventType.FACE_RECOGNIZED) {
            knownPersons.firstOrNull()?.name ?: "Family Member"
        } else if (eventType == EventType.UNKNOWN_PERSON) {
            "Unknown Individual"
        } else null

        val event = SecurityEvent(
            cameraId = camera.id,
            cameraName = camera.name,
            timestamp = System.currentTimeMillis(),
            eventType = eventType,
            personName = matchedPerson,
            confidence = if (eventType == EventType.UNKNOWN_PERSON) 0.95f else 0.91f,
            clipDurationSeconds = preferences.preBufferSeconds + preferences.postBufferSeconds,
            details = "CP PLUS Smart Motion triggered: 5s pre-buffer + 15s post-buffer archived."
        )

        val eventId = database.securityEventDao().insertEvent(event)
        val savedEvent = event.copy(id = eventId)

        // Check quiet hours
        val inQuietHours = isInQuietHours()
        if (preferences.notificationsEnabled && (!preferences.quietHoursEnabled || !inQuietHours)) {
            val shouldNotify = when (eventType) {
                EventType.PERSON_DETECTED -> preferences.notifyPerson
                EventType.UNKNOWN_PERSON -> preferences.notifyUnknownPerson
                EventType.FACE_RECOGNIZED -> preferences.notifyPerson
                EventType.MOTION_DETECTED, EventType.ZONE_INTRUSION -> preferences.notifyMotion
            }
            if (shouldNotify) {
                notificationHelper.showSecurityEventNotification(savedEvent)
            }
        }
    }

    private fun isInQuietHours(): Boolean {
        return try {
            val startParts = preferences.quietHoursStart.split(":")
            val endParts = preferences.quietHoursEnd.split(":")
            val now = Calendar.getInstance()
            val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
            val startMinutes = startParts[0].toInt() * 60 + startParts[1].toInt()
            val endMinutes = endParts[0].toInt() * 60 + endParts[1].toInt()

            if (startMinutes <= endMinutes) {
                currentMinutes in startMinutes..endMinutes
            } else {
                currentMinutes >= startMinutes || currentMinutes <= endMinutes
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
