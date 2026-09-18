package com.shahin.eidi.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast
import com.shahin.eidi.variants.debugLog
import com.shahin.eidi.ADD_EVENT
import com.shahin.eidi.BROADCAST_ALARM
import com.shahin.eidi.BROADCAST_RESTART_APP
import com.shahin.eidi.BROADCAST_UPDATE_APP
import com.shahin.eidi.KEY_EXTRA_PRAYER
import com.shahin.eidi.KEY_EXTRA_PRAYER_TIME
import com.shahin.eidi.MONTH_NEXT_COMMAND
import com.shahin.eidi.MONTH_PREV_COMMAND
import com.shahin.eidi.MONTH_RESET_COMMAND
import com.shahin.eidi.R
import com.shahin.eidi.entities.PrayTime
import com.shahin.eidi.ui.calendar.AddEventData
import com.shahin.eidi.utils.logException
import com.shahin.eidi.utils.startAthan
import com.shahin.eidi.utils.startWorker
import com.shahin.eidi.utils.update
import com.shahin.eidi.utils.updateMonthWidget

class BroadcastReceivers : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        when (val action = intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            TelephonyManager.ACTION_PHONE_STATE_CHANGED,
            BROADCAST_RESTART_APP -> startWorker(context)

            Intent.ACTION_DATE_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> update(context, true)
            Intent.ACTION_TIME_CHANGED, Intent.ACTION_SCREEN_ON, BROADCAST_UPDATE_APP ->
                update(context, false)

            ADD_EVENT -> runCatching {
                val addEventIntent = AddEventData.upcoming().asIntent()
                context.startActivity(addEventIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }.onFailure(logException).onFailure {
                Toast.makeText(context, R.string.device_does_not_support, Toast.LENGTH_SHORT).show()
            }

            BROADCAST_ALARM -> {
                val key = PrayTime.fromName(intent.getStringExtra(KEY_EXTRA_PRAYER)) ?: return
                val intendedTime = intent.getLongExtra(KEY_EXTRA_PRAYER_TIME, 0).takeIf { it != 0L }
                debugLog("Alarms: AlarmManager for $key")
                startAthan(context, key, intendedTime)
            }

            null -> Unit
            else -> {
                if (action.startsWith(MONTH_PREV_COMMAND)) {
                    action.replace(MONTH_PREV_COMMAND, "").toIntOrNull()?.let { id ->
                        updateMonthWidget(context, id, -1)
                    }
                } else if (action.startsWith(MONTH_NEXT_COMMAND)) {
                    action.replace(MONTH_NEXT_COMMAND, "").toIntOrNull()?.let { id ->
                        updateMonthWidget(context, id, 1)
                    }
                } else if (action.startsWith(MONTH_RESET_COMMAND)) {
                    action.replace(MONTH_RESET_COMMAND, "").toIntOrNull()?.let { id ->
                        updateMonthWidget(context, id, 0)
                    }
                }
            }
        }
    }
}
