package com.dicoding.android.zsstoryapp.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.android.zsstoryapp.data.local.datastore.LoginPreferences
import com.dicoding.android.zsstoryapp.view.login.LoginViewModel
import com.dicoding.android.zsstoryapp.view.main.MainViewModel

class PrefViewModelFactory(private val pref: LoginPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> { MainViewModel(pref) as T }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> { LoginViewModel(pref) as T }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}