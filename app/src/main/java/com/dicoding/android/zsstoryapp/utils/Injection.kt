package com.dicoding.android.zsstoryapp.utils

import com.dicoding.android.zsstoryapp.data.Repository
import com.dicoding.android.zsstoryapp.data.remote.ApiConfig

object Injection {
    fun provideRepository(): Repository {
        val apiService = ApiConfig.getApiService()
        return Repository(apiService)
    }
}