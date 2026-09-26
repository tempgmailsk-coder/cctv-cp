package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Camera
import com.example.data.model.EventType
import com.example.data.model.KnownPerson
import com.example.data.model.SecurityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Camera::class, SecurityEvent::class, KnownPerson::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cameraDao(): CameraDao
    abstract fun securityEventDao(): SecurityEventDao
    abstract fun knownPersonDao(): KnownPersonDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cctv_sentinel_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Pre-populate with typical CP PLUS configuration and initial events
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                prepopulateDatabase(database)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateDatabase(db: AppDatabase) {
            val initialCameras = listOf(
                Camera(
                    name = "Front Gate (CP-PLUS)",
                    ipAddress = "192.168.1.108",
                    rtspPort = 554,
                    httpPort = 80,
                    channel = 1,
                    username = "admin",
                    passwordEncrypted = "CpPlus@2026",
                    modelPreset = "CP_PLUS_ORANGE",
                    streamType = "MAIN",
                    isOnline = true,
                    isArmed = true,
                    personOnlyFilter = true,
                    sensitivity = 85
                ),
                Camera(
                    name = "Driveway Entry",
                    ipAddress = "192.168.1.109",
                    rtspPort = 554,
                    httpPort = 80,
                    channel = 2,
                    username = "admin",
                    passwordEncrypted = "CpPlus@2026",
                    modelPreset = "CP_PLUS_ORANGE",
                    streamType = "SUB",
                    isOnline = true,
                    isArmed = true,
                    personOnlyFilter = true,
                    sensitivity = 75
                ),
                Camera(
                    name = "Backyard & Patio",
                    ipAddress = "192.168.1.110",
                    rtspPort = 554,
                    httpPort = 80,
                    channel = 3,
                    username = "admin",
                    passwordEncrypted = "CpPlus@2026",
                    modelPreset = "CP_PLUS_COSMIC",
                    streamType = "SUB",
                    isOnline = true,
                    isArmed = true,
                    personOnlyFilter = true,
                    sensitivity = 80
                ),
                Camera(
                    name = "Living Hall Entrance",
                    ipAddress = "192.168.1.111",
                    rtspPort = 554,
                    httpPort = 8899,
                    channel = 1,
                    username = "admin",
                    passwordEncrypted = "Admin@123",
                    modelPreset = "CP_PLUS_EZYKAM",
                    streamType = "MAIN",
                    isOnline = true,
                    isArmed = false,
                    personOnlyFilter = true,
                    sensitivity = 70
                )
            )
            db.cameraDao().insertCameras(initialCameras)

            val initialPersons = listOf(
                KnownPerson(
                    name = "Marcus Vance",
                    relationship = "Homeowner / Primary",
                    faceTag = "face_marcus_vance_tag",
                    confidenceBaseline = 0.90f
                ),
                KnownPerson(
                    name = "Elena Vance",
                    relationship = "Family / Spouse",
                    faceTag = "face_elena_vance_tag",
                    confidenceBaseline = 0.88f
                ),
                KnownPerson(
                    name = "Sam Wilson",
                    relationship = "Staff / Property Care",
                    faceTag = "face_sam_wilson_tag",
                    confidenceBaseline = 0.82f
                )
            )
            db.knownPersonDao().insertPersons(initialPersons)

            val now = System.currentTimeMillis()
            val initialEvents = listOf(
                SecurityEvent(
                    cameraId = 1,
                    cameraName = "Front Gate (CP-PLUS)",
                    timestamp = now - 1000 * 60 * 12, // 12 mins ago
                    eventType = EventType.UNKNOWN_PERSON,
                    personName = "Unidentified Individual",
                    confidence = 0.96f,
                    clipDurationSeconds = 20,
                    isRead = false,
                    isBookmarked = true,
                    details = "CP PLUS SMD Human Trigger. Zone A boundary crossed. No face match in registered library."
                ),
                SecurityEvent(
                    cameraId = 1,
                    cameraName = "Front Gate (CP-PLUS)",
                    timestamp = now - 1000 * 60 * 65, // ~1 hour ago
                    eventType = EventType.FACE_RECOGNIZED,
                    personName = "Marcus Vance",
                    confidence = 0.93f,
                    clipDurationSeconds = 18,
                    isRead = true,
                    isBookmarked = false,
                    details = "Face Recognition Match (93% confidence). Gate unlocked event."
                ),
                SecurityEvent(
                    cameraId = 2,
                    cameraName = "Driveway Entry",
                    timestamp = now - 1000 * 60 * 180, // ~3 hours ago
                    eventType = EventType.PERSON_DETECTED,
                    personName = "Courier Delivery",
                    confidence = 0.89f,
                    clipDurationSeconds = 20,
                    isRead = true,
                    isBookmarked = true,
                    details = "Person movement detected near package drop area. Tree and shadow motions filtered out."
                ),
                SecurityEvent(
                    cameraId = 3,
                    cameraName = "Backyard & Patio",
                    timestamp = now - 1000 * 60 * 320, // ~5 hours ago
                    eventType = EventType.ZONE_INTRUSION,
                    personName = "Perimeter Movement",
                    confidence = 0.87f,
                    clipDurationSeconds = 20,
                    isRead = true,
                    isBookmarked = false,
                    details = "IVS Tripwire crossed in grid zone 7, 11. Ignored foliage wind motion."
                )
            )
            db.securityEventDao().insertEvents(initialEvents)
        }
    }
}
