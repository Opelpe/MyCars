package com.pepe.mycars.app.ui.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserService
    @Inject
    constructor() : Service() {
        //    @Inject
//    lateinit var userRepository: UserRepository

        override fun onCreate() {
            super.onCreate()

//        userRepository.isUserLogged()
        }

        override fun onBind(intent: Intent?): IBinder? {
            return null
        }
    }
