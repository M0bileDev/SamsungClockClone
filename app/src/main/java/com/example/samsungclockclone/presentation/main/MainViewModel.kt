package com.example.samsungclockclone.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samsungclockclone.domain.permissions.PermissionsListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val permissionsListener: PermissionsListener
) : ViewModel() {

    sealed interface MainActions {
        data object DisplayNotificationPermission : MainActions
    }

    private val mainActions = Channel<MainActions>()
    val actions = mainActions.receiveAsFlow()

    init {
        viewModelScope.launch {
            permissionsListener
                .collectPermissionPostNotification()
                .collectLatest {
                    mainActions.send(MainActions.DisplayNotificationPermission)
                }
        }
    }

}