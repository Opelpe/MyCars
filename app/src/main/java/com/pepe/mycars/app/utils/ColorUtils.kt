package com.pepe.mycars.app.utils

import android.R
import android.content.Context
import android.content.res.ColorStateList

class ColorUtils(val context: Context) {

    val grey = context.resources.getColor(com.pepe.mycars.R.color.grey, context.theme)
    val white = context.resources.getColor(com.pepe.mycars.R.color.white, context.theme)
    val darkGreen =  context.resources.getColor(com.pepe.mycars.R.color.green_dark, context.theme)


    fun getButtonPrimeColorStateList(): ColorStateList {
        return ColorStateList(arrayOf(intArrayOf(-R.attr.state_pressed), intArrayOf(
            R.attr.state_pressed)), intArrayOf(
            white, grey))
    }
    fun getButtonSecondColorStateList(): ColorStateList {
        return ColorStateList(arrayOf(intArrayOf(-R.attr.state_pressed), intArrayOf(
            R.attr.state_pressed)), intArrayOf(
            grey, white))
    }

    fun getCreateAccountColorStateList(): ColorStateList {
        return  ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_pressed), intArrayOf(
            android.R.attr.state_pressed)), intArrayOf(
            darkGreen, grey))
    }

    fun getCheckBoxColorStateList(): ColorStateList {
        return  ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_pressed), intArrayOf(
            android.R.attr.state_pressed)),  intArrayOf(
            white, grey))
    }

}