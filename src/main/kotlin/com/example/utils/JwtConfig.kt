package com.example.utils

import com.auth0.jwt.JWT

import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.User
import io.ktor.server.auth.*
import java.util.*
import com.auth0.jwt.JWT as Auth0JWT

object JwtConfig {
    private const val secret = "yourSecretKey"
    private const val issuer = "ktor.io"
    private const val audience = "ktorAudience"


    fun generateToken(username: String, role: UserRole): String {
        val secret = "your-secret-key" // از فایل کانفیگ بخوانید
        val issuer = "projectplants"
        val audience = "projectplantsAudience"
        val validityInMs = 36_000_00 * 10 // اعتبار ۱۰ ساعت

        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", username)
            .withClaim("role", role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(Algorithm.HMAC256(secret))
    }

}