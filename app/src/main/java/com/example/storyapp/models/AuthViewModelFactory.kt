package com.example.storyapp.models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.local.preference.AuthPreferences
import com.example.storyapp.data.local.preference.AuthPreferencesViewModel
import com.example.storyapp.di.Injection

class AuthViewModelFactory(
    private val preference: AuthPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthPreferencesViewModel::class.java)) {
            return AuthPreferencesViewModel(preference) as T
        }
        throw IllegalArgumentException("Unknown view model class ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: AuthViewModelFactory? = null

        fun getInstance(context: Context): AuthViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AuthViewModelFactory(Injection.providePreferences(context))
            }.also { instance = it }
    }

}

