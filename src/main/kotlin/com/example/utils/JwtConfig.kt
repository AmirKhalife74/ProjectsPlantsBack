package com.example.utils

import com.auth0.jwt.JWT

import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.user.User
import com.example.data.model.auth.LoginResponse
import com.example.database.DataBase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import java.util.*

object JwtConfig {


    fun generateToken(username: String, role: UserRole): String {
        val secret = "your-secret-key" // از فایل کانفیگ بخوانید
        val issuer = "projectPlants"
        val audience = "projectPlantsAudience"
        val validityInMs = 36_000_00 * 24 // اعتبار ۱۰ ساعت

        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", username)
            .withClaim("role", role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(Algorithm.HMAC256(secret))
    }

    fun generateAdminToken(username: String,role: UserRole): String {
        val secret = "your-secret-key" // از فایل کانفیگ بخوانید
        val issuer = "projectPlants"
        val audience = "projectPlantsAudience"
        val validityInMs = 36_000_00 * 10 // اعتبار ۱۰ ساعت

        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", username)
            .withClaim("role", role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(Algorithm.HMAC256(secret))
    }

    fun generateRefreshToken(userId: String): String {
        val jwtIssuer = "projectPlants"
        val jwtSecret = "your-secret-key"
        return JWT.create()
            .withIssuer(jwtIssuer)
            .withSubject(userId)
            .withExpiresAt(Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000))) // 7 days
            .sign(Algorithm.HMAC256(jwtSecret))
    }

    suspend fun handleLogin(user: User): LoginResponse {
        val accessToken = generateToken(user.username, user.role)
        val refreshToken = generateRefreshToken(user.id.toString())

        // Save refresh token in the database
        val usersCollection = DataBase.database.getCollection<User>("users")
        usersCollection.updateOne(
            Filters.eq("_id", user.id),
            Updates.set("refreshToken", refreshToken)
        )

        return LoginResponse(accessToken, refreshToken)
    }

}