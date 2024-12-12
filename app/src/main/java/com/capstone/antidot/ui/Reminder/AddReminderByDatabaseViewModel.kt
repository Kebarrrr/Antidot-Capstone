package com.capstone.antidot.ui.Reminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.AntibioticsItem
import kotlinx.coroutines.launch

class AddReminderByDatabaseViewModel (application: Application) : AndroidViewModel(application) {

    private val _antibiotic = MutableLiveData<AntibioticsItem>()
    val antibiotic: LiveData<AntibioticsItem> get() = _antibiotic

    fun getAntibioticById(antibioticID: String) {
        viewModelScope.launch {
            val antibiotic = RetrofitClient.getInstance(getApplication()).getDetailAntibiotics(antibioticID)
            _antibiotic.postValue(antibiotic.antibioticByID)
        }
    }
}