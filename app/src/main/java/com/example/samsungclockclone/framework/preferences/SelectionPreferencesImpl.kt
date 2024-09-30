package com.example.samsungclockclone.framework.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.samsungclockclone.framework.ext.userSelectionDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SelectionPreferencesImpl @Inject constructor(
    @ApplicationContext val context: Context
) : SelectionPreferences {

    companion object {
        const val NOTIFICATION_ASK_AGAIN_KEY = "NOTIFICATION_ASK_AGAIN_KEY"
    }

    private val notificationAskAgainKey = booleanPreferencesKey(NOTIFICATION_ASK_AGAIN_KEY)

    override suspend fun saveNotificationPermissionAskAgainEnabled(status: Boolean) {
        context.userSelectionDataStore.edit { selectPref ->
            selectPref[notificationAskAgainKey] = status
        }
    }

    override fun collectNotificationPermissionAskAgainEnabled(): Flow<Boolean> {
        return context.userSelectionDataStore.data.map { selectPref ->
            selectPref[notificationAskAgainKey] ?: true
        }
    }
}