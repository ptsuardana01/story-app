package com.example.storyapp.data.remote.api

import com.example.storyapp.data.remote.responses.AddNewStoryResponse
import com.example.storyapp.data.remote.responses.LoginResponse
import com.example.storyapp.data.remote.responses.RegisterResponse
import com.example.storyapp.data.remote.responses.StoryResponse
import com.example.storyapp.data.remote.responses.DetailStoryResponse
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
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ) : StoryResponse

    @GET("stories/{id}")
    fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ) : Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
    ) : Call<AddNewStoryResponse>
}