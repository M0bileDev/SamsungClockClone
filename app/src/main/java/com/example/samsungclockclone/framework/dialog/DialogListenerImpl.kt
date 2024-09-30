package com.example.samsungclockclone.framework.dialog

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class DialogListenerImpl @Inject constructor() : DialogListener {

    private val _displayPermissionPostNotificationDialog = Channel<Boolean>()
    private val displayPostNotificationDialog =
        _displayPermissionPostNotificationDialog.receiveAsFlow()

    private val _displayShortInfoDialog = Channel<Boolean>()
    private val displayShortInfoDialog = _displayShortInfoDialog.receiveAsFlow()

    override suspend fun changedVisibilityPermissionPostNotificationDialog(isVisible: Boolean) {
        _displayPermissionPostNotificationDialog.send(isVisible)
    }

    override fun collectVisibilityDisplayPermissionPostNotificationDialog(): Flow<Boolean> {
        return displayPostNotificationDialog
    }

    override suspend fun changedVisibilityShortInfoDialog(isVisible: Boolean) {
        _displayShortInfoDialog.send(isVisible)
    }

    override fun collectVisibilityDisplayShortInfoDialog(): Flow<Boolean> {
        return displayShortInfoDialog
    }

}