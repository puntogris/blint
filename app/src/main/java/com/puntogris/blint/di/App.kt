package com.puntogris.blint.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application(){

    override fun onCreate() {
        super.onCreate()
        //Debugging
        Timber.plant(Timber.DebugTree())
    }
}