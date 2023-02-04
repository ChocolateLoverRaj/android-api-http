package com.chocolateloverraj.android_api_http

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // FIXME: Switch is still on
        context.stopService(Intent(context, HttpServerService::class.java))
    }
}