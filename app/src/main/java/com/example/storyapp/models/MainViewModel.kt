package com.example.storyapp.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.local.AuthPreferences
import com.example.storyapp.data.responses.AllStoriesResponse
import com.example.storyapp.data.responses.DetailStoryResponse
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

    private val _detailStory = MutableLiveData<DetailStoryResponse>()
    val detailStory: LiveData<DetailStoryResponse> = _detailStory

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
                if (response.isSuccessful) {
                    _listStory.value = response.body()?.listStory
                    Log.d("DL", "data: ${_listStory.value.toString()}")
                } else {
                    _msg.value = response.message()
                    Log.d("DL", "msg: ${_msg.value.toString()}")
                }
            }

            override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _msg.value = t.message.toString()
                Log.d("DL", "data: ${_msg.value.toString()}")
            }

        })
    }

    fun getDetailStory(id: String, token: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService("Bearer $token").getDetailStory(id)
        client.enqueue(object: Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>,
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _detailStory.value = response.body()
                } else {
                    _msg.value = response.message()
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _msg.value = t.message.toString()
            }

        })
    }


}