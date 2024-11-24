package com.example.routing

import com.example.data.model.auth.LoginRequest
import com.example.data.model.auth.RegisterRequest
import com.example.data.repositories.UserRepository
import com.example.utils.JwtConfig
import com.example.utils.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAuthRouting(userRepository: UserRepository) {
    routing {
        post("/login") {

            val loginRequest = call.receive<LoginRequest>()
            val user = userRepository.getUserByUsername(loginRequest.username)

            if (user != null && userRepository.verifyPassword(loginRequest.password, user.passwordHash)) {
                val token = JwtConfig.generateToken(user.username, user.role)
                call.respond(mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Inva lid credentials")
            }
        }

        post("/register") {
            val registerRequest = call.receive<RegisterRequest>()
            // Password validation
            if (registerRequest.password != registerRequest.confirmPassword) {
                call.respond(HttpStatusCode.BadRequest, "Passwords do not match")
                return@post
            }

            // Check if username or email already exists
            val existingUser = userRepository.getUserByUsername(registerRequest.username)
            if (existingUser != null) {
                call.respond(HttpStatusCode.Conflict, "Username already taken")
                return@post
            }

            // Default to user role, but allow custom role from request
            val role = when (registerRequest.role) {
                UserRole.ADMIN -> UserRole.ADMIN
                UserRole.MODERATOR -> UserRole.MODERATOR
                else -> UserRole.USER  // Default to "user" if no role is specified or an invalid one is provided
            }

            // Save the user
            val createdUser = userRepository.createUser(
                username = registerRequest.username,
                password = registerRequest.password,
                registerRequest.email,
                role
            )

            call.respond(HttpStatusCode.Created, createdUser)

        }
    }
}

