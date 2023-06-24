package com.example.storyapp.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.api.ApiConfig
import com.example.storyapp.data.local.preference.AuthPreferences
import com.example.storyapp.data.remote.responses.LoginResponse
import com.example.storyapp.data.remote.responses.LoginResult
import com.example.storyapp.data.remote.responses.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {

    private val _login = MutableLiveData<LoginResult>()
    val login: LiveData<LoginResult> = _login

    private val _register = MutableLiveData<RegisterResponse>()
    val register: LiveData<RegisterResponse> = _register

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
                    _login.value = response.body()?.loginResult
                    Log.d("tokenVm", "token: ${response.body()?.loginResult}")
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
                    _register.value = response.body()
                    _msg.value = response.message()
                    login(email, password)
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
}
