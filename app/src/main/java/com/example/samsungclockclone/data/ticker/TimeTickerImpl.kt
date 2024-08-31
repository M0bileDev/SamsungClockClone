package com.example.samsungclockclone.data.ticker

import com.example.samsungclockclone.domain.ticker.TimeTicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimeTickerImpl @Inject constructor() : TimeTicker {

    private val job = Job()
    private val dispatcher = Dispatchers.Default
    private val coroutineScope = CoroutineScope(job + dispatcher)
    private val tickPass: MutableSharedFlow<Long> = MutableSharedFlow()

    override fun onTimeTick(
        milliseconds: Long,
    ) {
        coroutineScope.launch {
            tickPass.emit(milliseconds)
        }
    }

    override fun onGetTick(): Flow<Long> = tickPass.asSharedFlow()

    override fun onDestroy() {
        job.cancel()
    }
}