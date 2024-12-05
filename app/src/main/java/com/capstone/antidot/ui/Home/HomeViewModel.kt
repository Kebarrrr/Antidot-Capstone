package com.capstone.antidot.ui.Home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.ApiResponse
import com.capstone.antidot.utils.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _greetingText = MutableLiveData<String>()
    val greetingText: LiveData<String> get() = _greetingText

    private val _askText = MutableLiveData<String>()
    val askText: LiveData<String> get() = _askText

    private val sessionManager = SessionManager(application)

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            Log.e("HomeViewModel", "Token is null or empty")
            _greetingText.postValue("User tidak terautentikasi")
            return
        }

        viewModelScope.launch {
            try {
                val bearerToken = "Bearer $token"
                Log.d("HomeViewModel", "Using token: $bearerToken")

                // Make API call with the Bearer token
                val response = RetrofitClient.getInstance(getApplication()).userProfile(bearerToken)
                Log.d("HomeViewModel", "API Response: $response")

                processResponse(response)
            } catch (e: HttpException) {
                handleHttpException(e)
            } catch (e: Exception) {
                handleGeneralException(e)
            }
        }
    }


    private fun processResponse(response: ApiResponse) {
        if (response.status == "success" && response.userData != null) {
            val userName = response.userData.fullName
            Log.d("HomeViewModel", "User profile loaded: $userName")

            _greetingText.postValue("Hi, $userName")
            _askText.postValue("Apa yang bisa saya bantu hari ini?")
        } else {
            Log.e("HomeViewModel", "Invalid response: $response")
            _greetingText.postValue("Gagal mengambil profil")
        }
    }

    private fun handleHttpException(e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        Log.e("HomeViewModel", "HTTP Error: ${e.code()}, Error Body: $errorBody", e)
        _greetingText.postValue("Terjadi kesalahan API: ${e.code()}")
    }

    private fun handleGeneralException(e: Exception) {
        Log.e("HomeViewModel", "Exception occurred: ${e.localizedMessage}", e)
        _greetingText.postValue("Terjadi kesalahan umum")
    }
}
