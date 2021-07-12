package com.puntogris.blint.utils

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.HashMap
import kotlin.time.milliseconds
import kotlin.time.nanoseconds

object Converters {

    private val gson = Gson()

    @JvmStatic
    @TypeConverter
    fun toTimestamp(dateLong: Long?): Timestamp? {
        return dateLong?.let { Timestamp(it, 0) }
    }

    @JvmStatic
    @TypeConverter
    fun fromTimestamp(date: Timestamp?): Long? {
        return date?.seconds
    }
}