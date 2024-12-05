package com.capstone.antidot.ui.Antibiotics

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.AntibioticsItem

class AntibioticsViewModel(private val context: Context) : ViewModel() {

    private val _events = MutableLiveData<List<AntibioticsItem>>()
    val events: LiveData<List<AntibioticsItem>> = _events

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    suspend fun getAntibiotics() {
        _isLoading.value = true
        try {
            val response = RetrofitClient.getInstance(context).getAntibiotics()
            if (response.status == "success") {
                _events.value = response.antibiotics
            } else {
                _errorMessage.value = "Failed to load antibiotics data"
            }
        } catch (e: Exception) {
            _events.value = emptyList()
            _errorMessage.value = "Error fetching data: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
}
