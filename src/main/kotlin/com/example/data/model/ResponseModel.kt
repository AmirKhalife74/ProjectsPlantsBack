package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ResponseModel<T>(
    val status: Int,
    val isSuccessful:Boolean,
    val message: String,
    var data: T?
)

