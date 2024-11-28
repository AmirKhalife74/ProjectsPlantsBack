package com.example.data.model.user

import com.example.data.model.ObjectIdSerializer
import com.example.utils.UserRole
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class User(@Serializable(with = ObjectIdSerializer::class) val id: ObjectId? = ObjectId(),
                val username: String,
                val email: String,
                val passwordHash: String,
                val role: UserRole,
                var refreshToken: String? = null
)
