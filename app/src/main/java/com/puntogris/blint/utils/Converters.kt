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
    fun stringToMap(value: String): HashMap<String, String> {
        return gson.fromJson(value, object : TypeToken<HashMap<String, String>>() {}.type)
    }

    @JvmStatic
    @TypeConverter
    fun mapToString(value: HashMap<String, String>?): String {
        return if(value == null) "" else gson.toJson(value)
    }

    @JvmStatic
    @TypeConverter
    fun fromString(value: String): List<Int> {
        val type = object: TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, type)
    }

    @JvmStatic
    @TypeConverter
    fun fromList(list: List<Int>): String {
        val type = object: TypeToken<List<Int>>() {}.type
        return gson.toJson(list, type)
    }

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