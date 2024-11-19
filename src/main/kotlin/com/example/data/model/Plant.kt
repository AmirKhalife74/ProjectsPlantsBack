package com.example.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class Plant(@BsonId val id: Int, val name: String, val description: String, val wateringInterval: Int)
