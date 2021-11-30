package com.puntogris.blint.feature_store.data.data_source.local

import android.content.Context
import androidx.preference.PreferenceManager
import com.puntogris.blint.common.utils.Constants.SHOW_WELCOME_PREF
import com.puntogris.blint.common.utils.Constants.USER_HAS_BUSINESS_PREF
import com.puntogris.blint.common.utils.Keys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun showLoginScreen() = sharedPref.getBoolean(SHOW_WELCOME_PREF, true)

    fun setShowLoginScreen(value: Boolean) {
        sharedPref.edit().putBoolean(SHOW_WELCOME_PREF, value).apply()
    }

    fun getThemePref() = sharedPref.getString(Keys.THEME_PREF, "1")?.toInt() ?: 1

    fun showWelcomeScreen() = sharedPref.getBoolean(USER_HAS_BUSINESS_PREF, true)

    fun setShowNewUserScreenPref(value: Boolean) {
        sharedPref.edit().putBoolean(USER_HAS_BUSINESS_PREF, value).apply()
    }
}