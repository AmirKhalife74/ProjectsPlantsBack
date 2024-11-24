package com.example.data.repositories

import com.example.data.model.User
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

    suspend fun createUser(username: String, password: String, email: String, role: UserRole): User {
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val newUser = User(username = username, passwordHash = hashedPassword, email = email, role = role)
        return addUser(newUser)
    }

    suspend fun getAllUsers(): List<User> {
        return collection.find(User::role eq UserRole.USER).toList()
    }
}