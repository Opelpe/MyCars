package com.pepe.mycars.app

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyCarsApp : Application(){

    private var appContext: Context? = null


    fun get(): MyCarsApp {
        return appContext?.applicationContext as MyCarsApp
    }

    fun getAppContext(): Context? {
        return appContext
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}