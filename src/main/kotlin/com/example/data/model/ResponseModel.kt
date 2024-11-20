package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ResponseModel(
    val status: String,
    val message: String
)
