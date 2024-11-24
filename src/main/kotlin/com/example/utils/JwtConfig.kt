package com.example.utils

import com.auth0.jwt.JWT

import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.User
import io.ktor.server.auth.*
import java.util.*
import com.auth0.jwt.JWT as Auth0JWT

object JwtConfig {


    fun generateToken(username: String, role: UserRole): String {
        val secret = "your-secret-key" // از فایل کانفیگ بخوانید
        val issuer = "projectplants"
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

    fun generateAdminToken(username: String): String {
        val secret = "your-secret-key-admin" // از فایل کانفیگ بخوانید
        val issuer = "projectPlantsPanel"
        val audience = "projectPlantsAudiencePanel"
        val validityInMs = 36_000_00 * 10 // اعتبار ۱۰ ساعت

        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", username)
            .withClaim("role", "ADMIN")
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(Algorithm.HMAC256(secret))
    }

}