package com.pepe.mycars.app.data.domain.repository

interface UserRepository {

    fun getUserId(): Long
    fun getUserName(): String

    fun isUserLogged(): Boolean

    fun setUserLogged(isLogged: Boolean)
}