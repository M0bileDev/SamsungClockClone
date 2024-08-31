package com.example.samsungclockclone.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.samsungclockclone.domain.ticker.TimeTicker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimeTickReceiver : BroadcastReceiver() {

    @Inject
    lateinit var timeTicker: TimeTicker

    override fun onReceive(context: Context?, intent: Intent?) {
        timeTicker.onTimeTick(System.currentTimeMillis())
    }

}