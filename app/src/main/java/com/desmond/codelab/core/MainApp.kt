package com.desmond.codelab.core

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApp : Application(), LifecycleObserver {

    @Inject
    lateinit var settingContext: SettingsContext

    override fun onCreate() = super.onCreate().also {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
}