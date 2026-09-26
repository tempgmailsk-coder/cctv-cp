package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class SurveillancePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("cctv_sentinel_prefs", Context.MODE_PRIVATE)

    var isSystemArmed: Boolean
        get() = prefs.getBoolean("is_system_armed", true)
        set(value) = prefs.edit().putBoolean("is_system_armed", value).apply()

    var isPinLockEnabled: Boolean
        get() = prefs.getBoolean("is_pin_lock_enabled", false)
        set(value) = prefs.edit().putBoolean("is_pin_lock_enabled", value).apply()

    var securityPin: String
        get() = prefs.getString("security_pin", "1234") ?: "1234"
        set(value) = prefs.edit().putString("security_pin", value).apply()

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean("notifications_enabled", true)
        set(value) = prefs.edit().putBoolean("notifications_enabled", value).apply()

    var notifyPerson: Boolean
        get() = prefs.getBoolean("notify_person", true)
        set(value) = prefs.edit().putBoolean("notify_person", value).apply()

    var notifyUnknownPerson: Boolean
        get() = prefs.getBoolean("notify_unknown_person", true)
        set(value) = prefs.edit().putBoolean("notify_unknown_person", value).apply()

    var notifyMotion: Boolean
        get() = prefs.getBoolean("notify_motion", false)
        set(value) = prefs.edit().putBoolean("notify_motion", value).apply()

    var quietHoursEnabled: Boolean
        get() = prefs.getBoolean("quiet_hours_enabled", false)
        set(value) = prefs.edit().putBoolean("quiet_hours_enabled", value).apply()

    var quietHoursStart: String
        get() = prefs.getString("quiet_hours_start", "22:00") ?: "22:00"
        set(value) = prefs.edit().putString("quiet_hours_start", value).apply()

    var quietHoursEnd: String
        get() = prefs.getString("quiet_hours_end", "06:00") ?: "06:00"
        set(value) = prefs.edit().putString("quiet_hours_end", value).apply()

    var faceRecognitionEnabled: Boolean
        get() = prefs.getBoolean("face_rec_enabled", true)
        set(value) = prefs.edit().putBoolean("face_rec_enabled", value).apply()

    var faceConfidenceThreshold: Float
        get() = prefs.getFloat("face_rec_threshold", 0.80f)
        set(value) = prefs.edit().putFloat("face_rec_threshold", value).apply()

    var preBufferSeconds: Int
        get() = prefs.getInt("pre_buffer_seconds", 5)
        set(value) = prefs.edit().putInt("pre_buffer_seconds", value).apply()

    var postBufferSeconds: Int
        get() = prefs.getInt("post_buffer_seconds", 15)
        set(value) = prefs.edit().putInt("post_buffer_seconds", value).apply()

    var retentionDays: Int
        get() = prefs.getInt("retention_days", 14)
        set(value) = prefs.edit().putInt("retention_days", value).apply()

    var autoDeleteOldClips: Boolean
        get() = prefs.getBoolean("auto_delete_old_clips", true)
        set(value) = prefs.edit().putBoolean("auto_delete_old_clips", value).apply()
}
