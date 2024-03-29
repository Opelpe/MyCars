package com.pepe.mycars.app.utils

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

class IsLoggedInLiveData(private val sharedPreferences: SharedPreferences) :
    LiveData<Boolean>() {

    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "isLoggedIn") {
                value = sharedPreferences.getBoolean("isLoggedIn", false)
            }
        }

    override fun onActive() {
        super.onActive()
        value = sharedPreferences.getBoolean("isLoggedIn", false)
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}