package com.dicoding.android.zsstoryapp.view.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.android.zsstoryapp.data.Repository
import com.dicoding.android.zsstoryapp.data.model.ListStoryItem
import com.dicoding.android.zsstoryapp.utils.Injection

class StoryViewModel(private val repository: Repository) : ViewModel() {
    fun stories(): LiveData<PagingData<ListStoryItem>> = repository.getAllStory().cachedIn(viewModelScope)
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> { StoryViewModel(Injection.provideRepository()) as T }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}