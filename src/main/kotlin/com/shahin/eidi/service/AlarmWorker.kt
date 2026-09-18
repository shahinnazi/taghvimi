package com.shahin.eidi.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.shahin.eidi.variants.debugLog
import com.shahin.eidi.KEY_EXTRA_PRAYER
import com.shahin.eidi.KEY_EXTRA_PRAYER_TIME
import com.shahin.eidi.entities.PrayTime
import com.shahin.eidi.utils.startAthan
import kotlinx.coroutines.coroutineScope

class AlarmWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = coroutineScope {
        val key = inputData.getString(KEY_EXTRA_PRAYER)
        val intendedTime = inputData.getLong(KEY_EXTRA_PRAYER_TIME, 0).takeIf { it != 0L }
        debugLog("Alarms: WorkManager for $key")
        startAthan(applicationContext, PrayTime.fromName(key) ?: PrayTime.FAJR, intendedTime)
        Result.success()
    }
}
