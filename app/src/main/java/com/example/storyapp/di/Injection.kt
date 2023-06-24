package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.local.preference.AuthPreferences
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.api.ApiConfig

object Injection {
    fun provideRepository(context: Context) : StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }

    fun providePreferences(context: Context): AuthPreferences {
        return AuthPreferences.getInstance(context)
    }
}