package com.example.storyapp.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.api.ApiConfig
import com.example.storyapp.data.local.preference.AuthPreferences
import com.example.storyapp.data.remote.responses.AddNewStoryResponse
import com.example.storyapp.data.remote.responses.StoryResponse
import com.example.storyapp.data.remote.responses.DetailStoryResponse
import com.example.storyapp.data.remote.responses.ListStoryItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _listStoryLocation = MutableLiveData<List<ListStoryItem>>()
    val listStoryLocation: LiveData<List<ListStoryItem>> = _listStoryLocation

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _detailStory = MutableLiveData<DetailStoryResponse>()
    val detailStory: LiveData<DetailStoryResponse> = _detailStory

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg

    fun listStory(tkn: String): LiveData<PagingData<ListStoryItem>> =
        repository.getStory(tkn).cachedIn(viewModelScope)

    fun getDetailStory(id: String, token: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getDetailStory("Bearer $token", id)
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

    fun addNewStory(desc: RequestBody, photo: MultipartBody.Part, token: String, lat: Float?, lon: Float?) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().addNewStory("Bearer $token", desc, photo, lat, lon)
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

    fun getAllStoryWithLocation(token: String) {
        val client = ApiConfig.getApiService().getStoryWithLocation("Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    _listStoryLocation.value = response.body()?.listStory as List<ListStoryItem>
                } else {
                    Log.e("Maps", "onFailure : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e("Maps", "onFailure : ${t.message}")
            }
        })
    }


}