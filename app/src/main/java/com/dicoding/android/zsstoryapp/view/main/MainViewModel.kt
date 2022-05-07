package com.dicoding.android.zsstoryapp.view.main

import android.content.ContentValues
import android.os.Build
import android.service.controls.ControlsProviderService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.dicoding.android.zsstoryapp.data.local.entity.User
import com.dicoding.android.zsstoryapp.data.local.datastore.LoginPreferences
import com.dicoding.android.zsstoryapp.data.model.ListStoryItem
import com.dicoding.android.zsstoryapp.data.model.StoryResponse
import com.dicoding.android.zsstoryapp.data.remote.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: LoginPreferences) : ViewModel() {
    val listStory = MutableLiveData<List<ListStoryItem>>()

    // User Token
    fun getUser(): LiveData<User> { return pref.getUser().asLiveData() }

    // Story with Loc
    fun getStory(): LiveData<List<ListStoryItem>> { return listStory }
    fun setListStory(authToken: String) {
        val client = ApiConfig.getApiService().getStoriesLoc(authToken)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    listStory.postValue(response.body()?.listStory)
                } else {
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            @RequiresApi(Build.VERSION_CODES.R)
            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(ControlsProviderService.TAG, "Failure: ${t.message}")
            }
        })
    }

    // Logout
    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}