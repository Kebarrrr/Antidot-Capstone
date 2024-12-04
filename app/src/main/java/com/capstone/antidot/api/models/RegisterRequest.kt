package com.capstone.antidot.api.models

import java.util.Date

data class RegisterRequest(
    val fullName: String,
    val birthDate: Date,
    val email: String,
    val password: String,
    val confPassword: String
)
