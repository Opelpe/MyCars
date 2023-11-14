package com.pepe.mycars.app

import android.app.Application
import android.content.Context
import com.pepe.mycars.app.di.component.AppComponent
import com.pepe.mycars.app.di.component.DaggerAppComponent
import javax.inject.Inject

class MyCarsApp : Application(){

    private var appContext: Context? = null

    private val appComponent: AppComponent = DaggerAppComponent.create()

    fun get(): MyCarsApp {
        return appContext?.applicationContext as MyCarsApp
    }

    fun getAppContext(): Context? {
        return appContext
    }

    fun getAppComponent(): AppComponent {
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}