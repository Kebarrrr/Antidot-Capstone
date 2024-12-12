package com.capstone.antidot.ui.Reminder

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.RemindersItem
import kotlinx.coroutines.launch

class ReminderViewModel(private val context: Context) : ViewModel() {

    private val _reminders = MutableLiveData<List<RemindersItem>>()
    val reminders: LiveData<List<RemindersItem>> = _reminders

    private val apiService = RetrofitClient

    // Fungsi untuk mengambil data reminders dari API
    fun fetchReminders() {
        viewModelScope.launch {
            try {
                val response = apiService.getInstance(context).getReminder()
                if (response.status == "success") {
                    _reminders.postValue(response.reminders)
                } else {
                    _reminders.postValue(emptyList())
                }
            } catch (e: Exception) {
                _reminders.postValue(emptyList())
            }
        }
    }

    // Fungsi untuk menambahkan reminder baru dan memperbarui LiveData
    fun addReminder(reminder: RemindersItem) {
        val currentList = _reminders.value?.toMutableList() ?: mutableListOf()
        currentList.add(reminder)
        _reminders.postValue(currentList)
    }
}

