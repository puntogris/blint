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
    @JvmStatic
    @TypeConverter
    fun toTimestamp(dateLong: Long?): Timestamp? = dateLong?.let { Timestamp(Date(dateLong)) }

    @JvmStatic
    @TypeConverter
    fun fromTimestamp(date: Timestamp?): Long? = date?.toDate()?.time
}