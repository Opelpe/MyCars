package com.pepe.mycars.app.data.repository

import android.app.Application
import com.pepe.mycars.R
import com.pepe.mycars.app.data.domain.repository.MyRepository
import com.pepe.mycars.app.data.remote.MyApi
import javax.inject.Inject

class MyRepositoryImpl @Inject constructor(private val myApi: MyApi, private val appContext: Application): MyRepository {

    init {
        val appName = appContext.getString(R.string.app_name)
        println("Repository -> app name is : $appName")
    }


    override suspend fun doNetworkCall() {
        TODO("Not yet implemented")
    }
}