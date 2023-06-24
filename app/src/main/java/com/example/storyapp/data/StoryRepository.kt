package com.example.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.api.ApiService
import com.example.storyapp.data.remote.responses.ListStoryItem

class StoryRepository(
    private val database: StoryDatabase,
    private val apiService: ApiService
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(token, database, apiService),
            pagingSourceFactory = {
                database.storyDao().getAllStory()
            }
        ).liveData
    }
}