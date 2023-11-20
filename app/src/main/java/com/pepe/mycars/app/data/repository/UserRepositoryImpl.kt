package com.pepe.mycars.app.data.repository

import android.app.Application
import com.pepe.mycars.R
import com.pepe.mycars.app.data.domain.repository.UserRepository
import com.pepe.mycars.app.data.remote.MyApi
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val myApi: MyApi,  private val appContext: Application) : UserRepository {


    private var isLogged = false

    init {
        val appName = appContext.getString(R.string.app_name)
        println("UserRepository -> app name is : $appName")
    }

    override fun getUserId(): Long {
        TODO("Not yet implemented")
    }

    override fun getUserName(): String {
        TODO("Not yet implemented")
    }

    override fun isUserLogged(): Boolean {
        return isLogged
    }

    override fun setUserLogged(isLogged: Boolean) {
       this.isLogged = isLogged
    }
}