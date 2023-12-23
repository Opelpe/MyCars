package com.pepe.mycars.app.utils

sealed class ResourceOld<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : ResourceOld<T>(data)
    class Error<T>(message: String, data : T? = null) : ResourceOld<T>(data, message)
    class Loading<T>() : ResourceOld<T>()

}
