package com.example.samsungclockclone.presentation.activities.dismissAlarm

data class DismissAlarmUiState(
    val fireTime: Long = 0L,
    val name: String = ""
) {
    companion object {
        val previewDismissAlarmUiState = DismissAlarmUiState(
            1728332411,
            "Morning alarm"
        )
    }
}