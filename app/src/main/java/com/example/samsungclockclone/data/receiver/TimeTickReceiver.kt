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
        intent?.let {
            when (it.action) {
                Intent.ACTION_TIME_TICK, Intent.ACTION_TIME_CHANGED -> timeTicker.onTimeTick(System.currentTimeMillis())
                else -> {
                    //Nothing
                }
            }
        }


    }

}