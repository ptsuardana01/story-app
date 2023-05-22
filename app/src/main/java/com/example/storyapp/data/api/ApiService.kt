package com.example.storyapp.data.api

import com.example.storyapp.data.responses.AddNewStoryResponse
import com.example.storyapp.data.responses.LoginResponse
import com.example.storyapp.data.responses.RegisterResponse
import com.example.storyapp.data.responses.AllStoriesResponse
import com.example.storyapp.data.responses.DetailStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @GET("stories")
    fun getAllStories() : Call<AllStoriesResponse>

    @GET("stories/{id}")
    fun getDetailStory(
        @Path("id") id: String
    ) : Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
    ) : Call<AddNewStoryResponse>
}