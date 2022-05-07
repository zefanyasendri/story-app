package com.dicoding.android.zsstoryapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.android.zsstoryapp.data.model.ListStoryItem
import com.dicoding.android.zsstoryapp.data.remote.ApiService
import com.dicoding.android.zsstoryapp.utils.AppConstants
import com.dicoding.android.zsstoryapp.utils.AppConstants.INITIAL_PAGE_INDEX

class StoryPagingSource (private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(
                AppConstants.token,
                page,
                params.loadSize)
            val data = responseData.listStory
            LoadResult.Page(
                data = data,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (data.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}