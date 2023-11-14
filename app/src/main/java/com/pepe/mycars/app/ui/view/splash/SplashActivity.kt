package com.pepe.mycars.app.ui.view.splash

import android.os.Bundle
import android.util.Log
import com.pepe.mycars.app.MyCarsApp
import com.pepe.mycars.app.ui.common.BaseActivity
import com.pepe.mycars.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity() {

    private var binding : ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyCarsApp().getAppComponent().inject(this)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

    }
}