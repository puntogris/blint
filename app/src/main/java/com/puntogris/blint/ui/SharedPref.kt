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

    fun loginCompletedPref() = sharedPref.getBoolean(SHOW_WELCOME_PREF, false)

    fun setLoginCompletedPref(value: Boolean){
        sharedPref.edit().putBoolean(SHOW_WELCOME_PREF, value).apply()
    }

    fun getThemePref() = sharedPref.getString(THEME_PREF, "1")?.toInt() ?: 1

    fun showNewUserScreenPref() = sharedPref.getBoolean(USER_HAS_BUSINESS_PREF, true)

    fun setShowNewUserScreenPref(value:Boolean){
        sharedPref.edit().putBoolean(USER_HAS_BUSINESS_PREF, value).apply()
    }

    fun getCategoryFilterPref() = sharedPref.getString("category_filter_pref", "ALL")

    fun setCategoryFilterPref(value: String){
        sharedPref.edit().putString("category_filter_pref", value).apply()
    }

}