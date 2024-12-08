package com.capstone.antidot.ui.Antibiotics.DetailAntibiotics

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.AntibioticsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val _antibioticDetail = MutableLiveData<AntibioticsItem>()
    val antibioticDetail: LiveData<AntibioticsItem> = _antibioticDetail

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchDetailAntibiotics(antibioticId: String) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("DetailViewModel", "Sending antibiotic ID: $antibioticId")

                // Menggunakan Application Context dari AndroidViewModel
                val response = RetrofitClient.getInstance(getApplication()).getDetailAntibiotics(antibioticId)

                // Memastikan response berhasil dan mengubah LiveData
                if (response.status == "success") {
                    _antibioticDetail.postValue(response.antibioticByID)
                } else {
                    _errorMessage.postValue("Failed to load antibiotic details")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
