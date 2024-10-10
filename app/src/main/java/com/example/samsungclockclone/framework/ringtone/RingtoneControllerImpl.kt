package com.example.samsungclockclone.framework.ringtone

import android.media.Ringtone
import com.example.samsungclockclone.usecase.ringtone.RingtoneController
import javax.inject.Inject


class RingtoneControllerImpl @Inject constructor(
    private val ringtone: Ringtone
) : RingtoneController {
    override fun play() = ringtone.play()
    override fun stop() = ringtone.stop()
}