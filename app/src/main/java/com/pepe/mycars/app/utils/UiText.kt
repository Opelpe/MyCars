package com.pepe.mycars.app.utils

import android.content.Context
import androidx.annotation.StringRes
import com.pepe.mycars.R

sealed class UiText {
    data class DynamicString(val value: String) : UiText()

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any,
    ) : UiText()

    fun asString(context: Context): String =
        when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
}

fun Throwable.toUiText(): UiText = localizedMessage?.let(UiText::DynamicString) ?: UiText.StringResource(R.string.msg_unknown_error)
