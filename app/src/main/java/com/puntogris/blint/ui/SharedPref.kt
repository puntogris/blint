package com.puntogris.blint.ui

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPref @Inject constructor(@ApplicationContext private val context: Context) {
    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun setWelcomeUiPref(value:Boolean){
        sharedPref.edit().putBoolean("show_welcome_ui", value).apply()
    }

    fun getWelcomeUiPref() =
        sharedPref.getBoolean("show_welcome_ui", false)

    fun getThemePref() = sharedPref.getString("theme_pref", "1")?.toInt() ?: 1


}