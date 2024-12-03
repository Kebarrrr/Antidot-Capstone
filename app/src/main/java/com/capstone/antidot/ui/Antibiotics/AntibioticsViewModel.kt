package com.capstone.antidot.ui.Antibiotics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AntibioticsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Antibiotics Fragment"
    }
    val text: LiveData<String> = _text
}