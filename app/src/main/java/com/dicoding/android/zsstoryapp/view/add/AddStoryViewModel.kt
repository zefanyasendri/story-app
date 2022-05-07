package com.dicoding.android.zsstoryapp.view.add

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.android.zsstoryapp.data.model.AddStoryResponse
import com.dicoding.android.zsstoryapp.data.remote.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel : ViewModel() {
    private val _resMessage = MutableLiveData<String>()
    val resMessage : LiveData<String> = _resMessage

    // Story
    fun addStory(file: MultipartBody.Part, desc: RequestBody, token: String) {
        val client = ApiConfig.getApiService().addStory(file, desc, token)
        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(call: Call<AddStoryResponse>, response: Response<AddStoryResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _resMessage.value = response.message()
                    }
                } else {
                    _resMessage.value = response.message()
                    Log.e(TAG, "onFailure: ${response.message()}")
                    Log.e(TAG, "onFailure: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure : ${t.message}")
            }
        })
    }
}