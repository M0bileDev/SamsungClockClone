package com.example.samsungclockclone.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

const val ALARM_ID_KEY = "ALARM_ID"

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        println("LOGS alarm received")
//        TODO("Not yet implemented")
    }
}