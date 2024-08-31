package com.example.samsungclockclone.domain.ticker

import kotlinx.coroutines.flow.Flow

interface TimeTicker {
    fun onTimeTick(
        milliseconds: Long,
    )

    fun onGetTick(): Flow<Long>
    fun onDestroy()
}