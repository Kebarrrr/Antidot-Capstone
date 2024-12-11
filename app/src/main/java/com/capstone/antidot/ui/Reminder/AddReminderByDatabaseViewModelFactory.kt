package com.capstone.antidot.ui.Reminder

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddReminderByDatabaseViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Pastikan modelClass adalah AntibioticsViewModel dan buat instance-nya
        if (modelClass.isAssignableFrom(AddReminderByDatabaseViewModel::class.java)) {
            return AddReminderByDatabaseViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
