package com.dicoding.android.zsstoryapp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.android.zsstoryapp.data.local.datastore.LoginPreferences
import com.dicoding.android.zsstoryapp.data.local.entity.User
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: LoginPreferences) : ViewModel() {
    fun saveUser(user: User) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }
}