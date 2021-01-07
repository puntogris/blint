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


}