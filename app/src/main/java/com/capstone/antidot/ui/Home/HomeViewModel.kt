package com.capstone.antidot.ui.Home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.antidot.api.ApiService
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.UserResponse
import com.capstone.antidot.utils.SessionManager
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _userProfile = MutableLiveData<UserResponse?>()
    val userProfile: LiveData<UserResponse?> get() = _userProfile

    private val apiService: ApiService = RetrofitClient.getInstance(application)

    fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                val token = SessionManager(getApplication()).getToken()
                if (token != null) {
                    val response = apiService.userProfile()
                    _userProfile.postValue(response)
                } else {
                    // Handle token absence (user not logged in or expired session)
                    _userProfile.postValue(null)
                }
            } catch (e: Exception) {
                // Handle error
                _userProfile.postValue(null)
            }
        }
    }
}