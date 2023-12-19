package com.pepe.mycars.app

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyCarsApp : Application(){

    //todo po co?
    private var appContext: Context? = null


    //todo po co?
    fun get(): MyCarsApp {
        return appContext?.applicationContext as MyCarsApp
    }

    //todo po co?
    fun getAppContext(): Context? {
        return appContext
    }

    //todo po co?
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}