package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.EventType

class Converters {
    @TypeConverter
    fun fromEventType(value: EventType): String {
        return value.name
    }

    @TypeConverter
    fun toEventType(value: String): EventType {
        return try {
            EventType.valueOf(value)
        } catch (e: Exception) {
            EventType.PERSON_DETECTED
        }
    }
}
