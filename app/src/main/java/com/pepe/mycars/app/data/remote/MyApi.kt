package com.pepe.mycars.app.data.remote

import retrofit2.http.GET

interface MyApi {
    @GET("test")
    suspend fun doNetworkCall()
}