package com.pepe.mycars.app.utils

import android.R
import android.content.Context
import android.content.res.ColorStateList

class ColorUtils(val context: Context) {

    val grey = context.resources.getColor(com.pepe.mycars.R.color.grey, context.theme)
    val white = context.resources.getColor(com.pepe.mycars.R.color.white, context.theme)

    fun getImageColorStateList(): ColorStateList {
        return ColorStateList(arrayOf(intArrayOf(-R.attr.state_pressed), intArrayOf(
            R.attr.state_pressed)), intArrayOf(
            grey, white))
    }

}