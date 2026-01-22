package com.pepe.mycars.data.dto

sealed class CommonApiResponse<out T> {
    object Loading : CommonApiResponse<Nothing>()

    data class Error(val message: String) : CommonApiResponse<Nothing>()

    data class Success<T>(val data: T) : CommonApiResponse<T>()
}
