package com.pepe.mycars.app.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

fun Context.displayToast(massage: String) {
    Toast.makeText(this, massage, Toast.LENGTH_SHORT).show()
}

fun Context.logMessage(message: String) {
    Log.d("LOG_MESSAGE", message)
}
