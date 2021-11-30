package com.puntogris.blint.feature_store.data.data_source.local

import android.content.Context
import androidx.preference.PreferenceManager
import com.puntogris.blint.common.utils.Keys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun showLoginScreen() = sharedPref.getBoolean(Keys.SHOW_LOGIN_SCREEN_PREF, true)

    fun setShowLoginScreen(value: Boolean) {
        sharedPref.edit().putBoolean(Keys.SHOW_LOGIN_SCREEN_PREF, value).apply()
    }

    fun getTheme() = sharedPref.getString(Keys.THEME_PREF, "1")?.toInt() ?: 1

    fun showNewUserScreen() = sharedPref.getBoolean(Keys.SHOW_NEW_USER_SCREEN_PREF, true)

    fun setShowNewUserScreen(value: Boolean) {
        sharedPref.edit().putBoolean(Keys.SHOW_NEW_USER_SCREEN_PREF, value).apply()
    }
}