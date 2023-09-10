package com.puntogris.blint.feature_store.data.data_source.local

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.puntogris.blint.common.utils.Keys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun showLoginScreen(): Boolean {
        return sharedPref.getBoolean(Keys.SHOW_LOGIN_SCREEN_PREF, true)
    }

    fun setShowLoginScreen(value: Boolean) {
        sharedPref.edit().putBoolean(Keys.SHOW_LOGIN_SCREEN_PREF, value).apply()
    }

    fun getTheme(): String? {
        return sharedPref.getString(Keys.THEME_PREF, AppCompatDelegate.MODE_NIGHT_NO.toString())
    }

    fun showNewUserScreen() = sharedPref.getBoolean(Keys.SHOW_NEW_USER_SCREEN_PREF, true)

    fun setShowNewUserScreen(value: Boolean) {
        sharedPref.edit().putBoolean(Keys.SHOW_NEW_USER_SCREEN_PREF, value).apply()
    }
}
