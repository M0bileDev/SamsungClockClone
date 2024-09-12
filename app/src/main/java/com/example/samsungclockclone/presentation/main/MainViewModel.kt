package com.example.samsungclockclone.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.domain.dialog.DialogListener
import com.example.samsungclockclone.domain.permissions.PermissionsListener
import com.example.samsungclockclone.domain.preferences.SelectionPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dialogListener: DialogListener,
    private val permissionsListener: PermissionsListener,
    private val selectionPreferences: SelectionPreferences
) : ViewModel() {

    private val displayNotificationPermissionDialog = MutableStateFlow(false)
    private val displayShortINfoDialog = MutableStateFlow(false)

    val uiState = combine(
        displayNotificationPermissionDialog,
        displayShortINfoDialog
    ) { displayNotificationPermissionDialog, displaySettingsDialog ->

        MainActivityUiState(
            displayNotificationPermissionDialog,
            displaySettingsDialog
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        MainActivityUiState()
    )

    init {
        viewModelScope.launch {
            dialogListener
                .collectVisibilityDisplayPermissionPostNotificationDialog()
                .collectLatest {
                    displayNotificationPermissionDialog.value = it
                }
        }
        viewModelScope.launch {
            dialogListener
                .collectVisibilityDisplayShortInfoDialog()
                .collectLatest {
                    displayShortINfoDialog.value = it
                }
        }
    }

    fun onRequestNotificationPermission(neverAskAgainEnabled: Boolean) {
        viewModelScope.launch {
            permissionsListener.requestPermissionPostNotification(Unit)
        }
    }

    fun onDismissNotificationPermission(neverAskAgainEnabled: Boolean) {
        viewModelScope.launch {
            dialogListener.changedVisibilityPermissionPostNotificationDialog(false)
        }
    }

    fun onDismissShortInfoDialog() {
        viewModelScope.launch {
            dialogListener.changedVisibilityShortInfoDialog(false)
        }

    }

}