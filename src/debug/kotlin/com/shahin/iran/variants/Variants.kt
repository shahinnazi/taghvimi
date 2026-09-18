package com.shahin.eidi.variants

import android.util.Log
import com.shahin.eidi.LOG_TAG

fun debugLog(vararg message: Any?) {
    Log.d(LOG_TAG, message.joinToString(", "))
}

inline val <T> T.debugAssertNotNull: T inline get() = checkNotNull(this)
