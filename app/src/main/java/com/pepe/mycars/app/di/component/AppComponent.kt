package com.pepe.mycars.app.di.component

import com.pepe.mycars.app.ui.view.splash.SplashActivity
import dagger.Component

@Component
interface AppComponent {

    fun inject(splashActivity: SplashActivity)

}