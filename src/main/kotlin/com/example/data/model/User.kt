package com.example.data.model

import com.example.utils.UserRole
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(@Serializable(with = ObjectIdSerializer::class) val id: ObjectId? = ObjectId(),
                val username: String,
                val email: String,
                val passwordHash: String,
                val role: UserRole,
                var refreshToken: String? = null
)
