package com.puntogris.blint.common.utils

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.util.*

object Converters {
    @JvmStatic
    @TypeConverter
    fun toTimestamp(dateLong: Long?): Timestamp? = dateLong?.let { Timestamp(Date(dateLong)) }

    @JvmStatic
    @TypeConverter
    fun fromTimestamp(date: Timestamp?): Long? = date?.toDate()?.time
}