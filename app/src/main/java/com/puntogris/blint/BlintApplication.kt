package com.puntogris.blint

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.puntogris.blint.feature_store.data.data_source.local.SharedPreferences
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BlintApplication : Application() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()


        AppCompatDelegate.setDefaultNightMode(sharedPreferences.getTheme())
    }
}