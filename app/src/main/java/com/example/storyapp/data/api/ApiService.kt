package com.example.storyapp.data.api

import com.example.storyapp.data.responses.LoginResponse
import com.example.storyapp.data.responses.RegisterResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ) : Call<LoginResponse>
}