package com.capstone.antidot.ui.Home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.UserData
import com.capstone.antidot.api.models.UserRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // LiveData for observing user profile
    private val _userProfile = MutableLiveData<UserData>()
    val userProfile: LiveData<UserData> get() = _userProfile

    // Load user profile from API
    fun loadUserProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Replace with your actual API call to get user profile
                val response = RetrofitClient.instance.userProfile(UserData(fullName, profilePicture)) // Assuming the API is set up
                withContext(Dispatchers.Main) {
                    // Post the user profile data to LiveData
                    _userProfile.postValue(response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error (e.g., show an error message)
            }
        }
    }
}
