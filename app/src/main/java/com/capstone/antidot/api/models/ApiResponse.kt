package com.capstone.antidot.api.models

data class ApiResponse(
    val status: String,
    val message: String,
    val token: String?,
    val userData: UserData?
)