package com.example.samsungclockclone.framework.permissions

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class PermissionsListenerImpl @Inject constructor() : PermissionsListener {

    private val permissionPostNotification = Channel<Unit>()
    private val postNotification = permissionPostNotification.receiveAsFlow()

    override suspend fun requestPermissionPostNotification(request: Unit) {
        permissionPostNotification.send(request)
    }

    override fun collectPermissionPostNotification(): Flow<Unit> {
        return postNotification
    }

}