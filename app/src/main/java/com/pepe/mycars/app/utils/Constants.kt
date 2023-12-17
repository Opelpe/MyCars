package com.pepe.mycars.app.utils

object SharedPrefConstants {
    val LOCAL_SHARED_PREF = "local_shared_pref"
    val USER_SESSION = "user_session"
}

object FireStoreDocumentField {
    val ACCOUNT_PROVIDER_GOOGLE  : String = "google.com"
    val ACCOUNT_PROVIDER_EMAIL  : String = "email&password"
    val ACCOUNT_PROVIDER_ANONYMOUS  : String = "anonymous"
}

object FireStoreCollection{
    val USER = "User"
    val USER_GUEST = "Anonymous"
}