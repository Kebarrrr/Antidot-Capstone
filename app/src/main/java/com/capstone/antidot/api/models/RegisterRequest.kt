package com.capstone.antidot.api.models

data class RegisterRequest(
    val name: String,
    val date: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)
