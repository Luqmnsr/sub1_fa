package com.example.mybottomnavigation.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SettingPreference) : ViewModel() {
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getNotificationSetting(): LiveData<Boolean> {
        return pref.getNotificationSetting().asLiveData()
    }

    fun saveNotificationSetting(isEnabled: Boolean) {
        viewModelScope.launch {
            pref.saveNotificationSetting(isEnabled)
        }
    }
}