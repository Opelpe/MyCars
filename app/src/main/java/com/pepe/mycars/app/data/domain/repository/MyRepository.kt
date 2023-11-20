package com.pepe.mycars.app.data.domain.repository

interface MyRepository {

    suspend fun doNetworkCall()
}