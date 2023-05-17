package com.example.storyapp.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.local.AuthPreferences
import com.example.storyapp.data.responses.AllStoriesResponse
import com.example.storyapp.data.responses.ListStoryItem
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: AuthPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    fun getAllListStory(token : String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService("Bearer $token").getAllStories()
        client.enqueue(object : Callback<AllStoriesResponse> {
            override fun onResponse(
                call: Call<AllStoriesResponse>,
                response: Response<AllStoriesResponse>,
            ) {
                _isLoading.value = false
                if (response.isSuccessful) _listStory.value = response.body()?.listStory else _msg.value = response.message()
            }

            override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _msg.value = t.message.toString()
            }

        })
    }


}