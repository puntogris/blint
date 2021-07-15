package com.puntogris.blint.ui

import android.content.Context
import androidx.preference.PreferenceManager
import com.puntogris.blint.utils.Constants.SHOW_WELCOME_PREF
import com.puntogris.blint.utils.Constants.THEME_PREF
import com.puntogris.blint.utils.Constants.USER_HAS_BUSINESS_PREF
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPref @Inject constructor(@ApplicationContext private val context: Context) {
    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun setWelcomeUiPref(value:Boolean){
        sharedPref.edit().putBoolean(SHOW_WELCOME_PREF, value).apply()
    }

    fun getWelcomeUiPref() =
        sharedPref.getBoolean(SHOW_WELCOME_PREF, false)

    fun getThemePref() = sharedPref.getString(THEME_PREF, "1")?.toInt() ?: 1

    fun getUserHasBusinessPref() = sharedPref.getBoolean(USER_HAS_BUSINESS_PREF, false)

    fun setUserHasBusinessPref(value:Boolean){
        sharedPref.edit().putBoolean(USER_HAS_BUSINESS_PREF, value).apply()
    }

}