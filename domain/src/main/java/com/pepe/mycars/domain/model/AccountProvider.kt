package com.pepe.mycars.domain.model

enum class AccountProvider(val value: String) {
    GOOGLE("google.com"),
    EMAIL("email&password"),
    ANONYMOUS("anonymous");

    companion object {
        fun fromValue(value: String?): AccountProvider {
            return when (value) {
                GOOGLE.value -> GOOGLE
                EMAIL.value -> EMAIL
                ANONYMOUS.value -> ANONYMOUS
                else -> throw IllegalArgumentException("Invalid AccountProvider value: $value")
            }
        }
    }
}
