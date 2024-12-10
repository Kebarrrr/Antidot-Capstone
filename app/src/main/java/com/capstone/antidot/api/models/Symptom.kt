package com.capstone.antidot.api.models

data class Symptom(
    val name: String,
    val isUserAdded: Boolean = false // Defaultnya bawaan (false)
)