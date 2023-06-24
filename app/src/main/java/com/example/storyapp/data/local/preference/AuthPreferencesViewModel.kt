package com.example.storyapp.data.local.preference

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthPreferencesViewModel (
    private val pref: AuthPreferences
) : ViewModel() {
    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun logout() {
        viewModelScope.launch {
            pref.saveToken("")
        }
    }
}