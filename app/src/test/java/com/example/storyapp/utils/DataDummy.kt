package com.example.storyapp.utils

import com.example.storyapp.data.remote.responses.ListStoryItem
import com.example.storyapp.data.remote.responses.LoginResult
import com.example.storyapp.data.remote.responses.RegisterResponse

object DataDummy {

    fun dummyDataEntity(): List<ListStoryItem> {
        val storyList: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..25) {
            val story = ListStoryItem(
                "story-$i",
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "created + $i",
                "name + $i",
                "description + $i",
                null,
                null,
            )
            storyList.add(story)
        }
        return storyList
    }

    fun dummyDataWithLocationEntity(): List<ListStoryItem> {
        val storyList: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..25) {
            val story = ListStoryItem(
                "story-$i",
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "created + $i",
                "name + $i",
                "description + $i",
                i.toDouble(),
                i.toDouble(),
            )
            storyList.add(story)
        }
        return storyList
    }

    fun emptyDummyDataEntity(): List<ListStoryItem> {
        return emptyList()
    }

    fun dummyLoginEntity() = LoginResult(
        "name",
        "id",
        "token"
    )

    fun dummyRegisterEntity() = RegisterResponse(
        false,
        "success"
    )
}