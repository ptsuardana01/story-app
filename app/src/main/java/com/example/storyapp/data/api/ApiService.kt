package com.example.storyapp.data.api

import com.example.storyapp.data.responses.AddNewStoryResponse
import com.example.storyapp.data.responses.AllStoriesResponse
import com.example.storyapp.data.responses.DetailStoryResponse
import com.example.storyapp.data.responses.LoginResponse
import com.example.storyapp.data.responses.RegisterResponse
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
        @Field("password") password: String,
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): AllStoriesResponse

    @GET("stories/{id}")
    fun getDetailStory(
        @Path("id") id: String,
    ): Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
    ): Call<AddNewStoryResponse>

    @GET("stories?location=1")
    fun getStoryLocation(
        @Query("size") size: Int = 100,
    ): Call<AllStoriesResponse>
}