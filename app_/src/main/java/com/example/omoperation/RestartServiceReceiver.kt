package com.example.omoperation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RestartServiceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED || intent?.action == "android.intent.action.RESTART_SERVICE") {
            context?.startForegroundService(Intent(context, LocationService::class.java))
        }
    }
}