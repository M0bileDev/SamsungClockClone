package com.example.samsungclockclone.abstraction.preferences

import kotlinx.coroutines.flow.Flow

interface SelectionPreferences {

    suspend fun saveNotificationPermissionAskAgainEnabled(status: Boolean)
    fun collectNotificationPermissionAskAgainEnabled(): Flow<Boolean>

}