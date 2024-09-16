package com.example.samsungclockclone.data.local.permissions

import com.example.samsungclockclone.domain.permissions.PermissionsListener
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