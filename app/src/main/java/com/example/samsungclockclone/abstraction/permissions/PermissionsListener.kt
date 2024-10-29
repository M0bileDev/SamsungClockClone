package com.example.samsungclockclone.abstraction.permissions

import kotlinx.coroutines.flow.Flow

interface PermissionsListener {

    suspend fun requestPermissionPostNotification(request: Unit)
    fun collectPermissionPostNotification(): Flow<Unit>
}