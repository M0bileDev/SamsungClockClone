package com.example.samsungclockclone.usecase.permissions

import kotlinx.coroutines.flow.Flow

interface PermissionsListener {

    suspend fun requestPermissionPostNotification(request: Unit)
    fun collectPermissionPostNotification(): Flow<Unit>
}