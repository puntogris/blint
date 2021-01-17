package com.puntogris.blint.ui

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPref @Inject constructor(@ApplicationContext private val context: Context) {
    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun setUserHasBusinessRegisteredPref(){
        sharedPref.edit().putBoolean("user_has_local_business", true).apply()
    }

    fun getUserHasBusinessRegisteredPref() =
        sharedPref.getBoolean("user_has_local_business", false)

    fun getThemePref() = sharedPref.getString("theme_pref", "1")?.toInt() ?: 1

    fun setCurrentBusinessPref(value:String){
        sharedPref.edit().putString("current_business", value).apply()
    }

    fun getCurrentBusinessPref() = sharedPref.getString("current_business","1")
}