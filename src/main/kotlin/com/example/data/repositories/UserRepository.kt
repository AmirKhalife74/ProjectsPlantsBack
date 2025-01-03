package com.example.data.repositories

import com.example.data.model.user.User
import com.example.data.model.auth.RegisterRequest
import com.example.utils.UserRole
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.mindrot.jbcrypt.BCrypt

class UserRepository(private val collection: CoroutineCollection<User>) {
    suspend fun getUserByUsername(username: String): User? {
        val user = collection.findOne(User::username eq username)
        return user
    }

    suspend fun verifyPassword(purePassword: String, hashedPassword: String): Boolean {
        val isPasswordVerified = BCrypt.checkpw(purePassword, hashedPassword)
        return isPasswordVerified
    }

    private suspend fun addUser(user: User): User {

        collection.insertOne(user)
        return user
    }

    suspend fun createUser(user:RegisterRequest): User {
        val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
        val newUser = User(username = user.username, passwordHash = hashedPassword, email = user.email, role = user.role)
        return addUser(newUser)
    }

    suspend fun getAllUsers(): List<User> {
        return collection.find(User::role eq UserRole.USER).toList()
    }

//    suspend fun getUserByID(id: String): User? {
//        return collection.findOne(User::id eq id)
//    }
}