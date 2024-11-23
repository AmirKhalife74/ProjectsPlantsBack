package com.example.data.model.auth

import com.example.utils.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String? = null,
    val role: UserRole = UserRole.USER
)
