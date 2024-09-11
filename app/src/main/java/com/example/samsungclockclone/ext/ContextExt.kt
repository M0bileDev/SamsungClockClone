package com.example.samsungclockclone.ext

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.alarmDataStore: DataStore<Preferences> by preferencesDataStore(name = "alarm")
val Context.userSelectionDataStore: DataStore<Preferences> by preferencesDataStore(name = "userSelection")