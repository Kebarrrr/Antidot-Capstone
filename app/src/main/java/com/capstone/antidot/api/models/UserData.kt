package com.capstone.antidot.api.models

data class UserData(
    val userID: Int,
    val fullName: String,
    val birthDate: String,
    val age: Int,
    val email: String,
    val profilePicture: String,
    val createdAt: String,
    val updatedAt: String
)