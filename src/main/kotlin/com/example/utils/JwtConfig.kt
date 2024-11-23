package com.example.utils

import com.auth0.jwt.JWT

import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.User
import io.ktor.server.auth.*
import com.auth0.jwt.JWT as Auth0JWT

object JwtConfig {
    private const val secret = "yourSecretKey"
    private const val issuer = "ktor.io"
    private const val audience = "ktorAudience"

    val verifier =
        JWT
            .require(
                Algorithm
                    .HMAC256(secret)
            )
            .withIssuer(issuer)
            .withAudience(audience).build()


    fun generateToken(user: User): String {
        return Auth0JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", user.username)
            .sign(Algorithm.HMAC256(secret))
    }

}