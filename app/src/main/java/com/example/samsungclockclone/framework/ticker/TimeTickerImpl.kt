package com.example.samsungclockclone.framework.ticker

import com.example.samsungclockclone.usecase.ticker.TimeTicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimeTickerImpl @Inject constructor() : TimeTicker {

    private var job: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val tickPass: MutableSharedFlow<Long> = MutableSharedFlow()

    override fun onTimeTick(
        milliseconds: Long,
    ) {
        job = coroutineScope.launch {
            tickPass.emit(milliseconds)
        }
    }

    override fun onGetTick(): Flow<Long> = tickPass.asSharedFlow()

    override fun onDestroy() {
        job?.cancel()
    }
}