package com.puntogris.blint.di

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.puntogris.blint.ui.SharedPreferences
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(){

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()


        AppCompatDelegate.setDefaultNightMode(sharedPreferences.getThemePref())
    }
}