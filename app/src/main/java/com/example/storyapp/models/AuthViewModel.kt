package com.example.storyapp.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.local.AuthPreferences
import com.example.storyapp.data.responses.LoginResponse
import com.example.storyapp.data.responses.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(private val pref: AuthPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String> = _msg


    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>,
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val result = response.body()?.loginResult
                    result?.token?.let {
                        saveToken(it)
                    }
                    Log.d("tokenVm", "token: ${result?.token}")
                } else {
                    _msg.value = response.message()
                    Log.d("tokenVm", "msg: ${_msg.value}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _msg.value = t.message.toString()
            }
        })
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>,
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.error == false) {
                        login(email, password)
                    }
                } else {
                    _msg.value = response.message()
                    Log.d("regisVm", "msg: ${_msg.value}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _msg.value = t.message.toString()
            }

        })
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun logout() {
        saveToken("")
    }
}
