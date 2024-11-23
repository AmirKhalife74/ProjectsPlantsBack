package com.example.data.model

import com.example.utils.UserRole
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(@BsonId val id: ObjectId? = ObjectId(),
                val username: String,
                val email: String,
                val passwordHash: String,
                val role: UserRole
)
