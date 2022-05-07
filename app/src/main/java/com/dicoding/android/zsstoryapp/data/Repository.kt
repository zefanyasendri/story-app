package com.dicoding.android.zsstoryapp.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.android.zsstoryapp.data.model.ListStoryItem
import com.dicoding.android.zsstoryapp.data.remote.ApiService

class Repository (private val apiService: ApiService) {
    fun getAllStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).liveData
    }
}