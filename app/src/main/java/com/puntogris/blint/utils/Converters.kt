package com.puntogris.blint.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {

    private val gson = Gson()

    @JvmStatic
    @TypeConverter
    fun stringToMap(value: String): HashMap<String, String> {
        return gson.fromJson(value,  object : TypeToken<HashMap<String, String>>() {}.type)
    }

    @JvmStatic
    @TypeConverter
    fun mapToString(value: HashMap<String, String>?): String {
        return if(value == null) "" else gson.toJson(value)
    }

}