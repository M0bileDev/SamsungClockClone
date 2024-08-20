package com.example.samsungclockclone.domain.ticker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class TimeTicker {

    private val job = Job()
    private val dispatcher = Dispatchers.Default
    private val coroutineScope = CoroutineScope(job + dispatcher)
    private val tickPass: MutableSharedFlow<Long> = MutableSharedFlow()

    fun onSendTick(
        milliseconds: Long,
    ) {
        coroutineScope.launch {
            tickPass.emit(milliseconds)
        }
    }

    fun onGetTick(): Flow<Long> = tickPass.asSharedFlow()

    fun onDestroy() {
        job.cancel()
    }
}