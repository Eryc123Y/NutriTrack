package com.example.fit2081a1_yang_xingyu_33533563.data.model

import androidx.room.TypeConverter
import java.util.Date

/**
 * This class provides type converters for Room to convert between Date and Long types.
 * It is used to store Date objects in the database as Long timestamps.
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
} 