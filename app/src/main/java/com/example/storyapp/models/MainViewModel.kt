package com.example.storyapp.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.api.ApiConfig
import com.example.storyapp.data.local.preference.AuthPreferences
import com.example.storyapp.data.remote.responses.AddNewStoryResponse
import com.example.storyapp.data.remote.responses.AllStoriesResponse
import com.example.storyapp.data.remote.responses.DetailStoryResponse
import com.example.storyapp.data.remote.responses.ListStoryItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
                } else {
                    _msg.value = response.message()
                }
            }

            override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _msg.value = t.message.toString()
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

    fun addNewStory(desc: RequestBody, photo: MultipartBody.Part, token: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService("Bearer $token").addNewStory(desc, photo)
        client.enqueue(object : Callback<AddNewStoryResponse> {
            override fun onResponse(
                call: Call<AddNewStoryResponse>,
                response: Response<AddNewStoryResponse>,
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _msg.value = response.body()?.message.toString()
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _msg.value = t.message.toString()
            }

        })
    }


}