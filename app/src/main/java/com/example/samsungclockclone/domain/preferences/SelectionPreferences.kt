package com.example.samsungclockclone.domain.preferences

import kotlinx.coroutines.flow.Flow

interface SelectionPreferences {

    suspend fun saveNotificationPermissionAskAgainEnabled(status: Boolean)
    fun collectNotificationPermissionAskAgainEnabled(): Flow<Boolean>

}