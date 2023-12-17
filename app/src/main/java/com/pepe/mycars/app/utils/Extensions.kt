package com.pepe.mycars.app.utils

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.os.Message
import android.util.Log
import android.widget.Toast
fun Context.displayToast(massage: String) {
        Toast.makeText(this, massage, Toast.LENGTH_SHORT).show()
    }

fun Context.logMessage(message: String){
    Log.d("LOG_MESSAGE", message)
}


