package com.capstone.antidot.api.models

data class Symptom(
    val symptoms: Array<String>,
    val isUserAdded: Boolean = false // Defaultnya bawaan (false)
)

data class SymptomsRequest(
    val selectedSymptoms: Array<String>, // Gejala yang dipilih oleh pengguna
    val initialSymptoms: Array<String>   // Gejala awal
)

