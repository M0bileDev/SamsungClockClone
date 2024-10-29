package com.example.samsungclockclone.abstraction.ticker

import kotlinx.coroutines.flow.Flow

interface TimeTicker {
    fun onTimeTick(
        milliseconds: Long,
    )

    fun onGetTick(): Flow<Long>
    fun onDestroy()
}