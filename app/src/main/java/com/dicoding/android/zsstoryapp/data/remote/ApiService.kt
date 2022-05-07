package com.dicoding.android.zsstoryapp.data.remote

import com.dicoding.android.zsstoryapp.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Register
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name :String,
        @Field("email") email :String,
        @Field("password") password :String
    ): Call<RegisterResponse>

    // Login
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email :String,
        @Field("password") password :String
    ): Call<LoginResponse>

    // Get All Stories for Paging
    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") authToken: String,
        @Query("page") page: Int? = 0,
        @Query("size") size: Int? = 10,
    ): StoryResponse

    // Get All Stories with Location for Maps
    @GET("stories")
    fun getStoriesLoc (
        @Header("Authorization") authToken: String,
        @Query("location") location: Int = 1,
    ): Call<StoryResponse>

    // Add Story
    @Multipart
    @POST("stories")
    fun addStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") authToken: String
    ): Call<AddStoryResponse>
}