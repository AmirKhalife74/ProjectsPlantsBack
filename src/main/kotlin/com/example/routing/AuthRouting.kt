package com.example.routing

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.example.data.model.ResponseModel
import com.example.data.model.user.User
import com.example.data.model.auth.LoginRequest
import com.example.data.model.auth.RegisterRequest
import com.example.data.repositories.UserRepository
import com.example.database.DataBase
import com.example.utils.JwtConfig
import com.example.utils.UserRole
import com.mongodb.client.model.Filters
import io.ktor.http.*
import io.ktor.server.application.*
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
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")

            }
        }

        post("/register") {
            val registerRequest = call.receive<RegisterRequest>()
            // Password validation
            if (registerRequest.password != registerRequest.confirmPassword) {
                val responseModel = ResponseModel<User>(status = 409, isSuccessful = true,message = "پسوورد یکسان نبود!",data = null)
                call.respond(HttpStatusCode.OK, responseModel)
                return@post
            }

            // Check if username or email already exists
            val existingUser = userRepository.getUserByUsername(registerRequest.username)
            if (existingUser != null) {
                val responseModel = ResponseModel<User>(status = 409, isSuccessful = true,message = "کاربر وجود دارد",data = null)
                call.respond(HttpStatusCode.Conflict, responseModel)
                return@post
            }

            // Default to user role, but allow custom role from request
            val role = when (registerRequest.role) {
                UserRole.ADMIN -> UserRole.ADMIN
                UserRole.MODERATOR -> UserRole.MODERATOR
                else -> UserRole.USER  // Default to "user" if no role is specified or an invalid one is provided
            }

            // Save the user
            val createdUser = userRepository.createUser(registerRequest)
            val responseMessage: ResponseModel<User> =
                ResponseModel(status = 200, isSuccessful = true, message = "عملیات با موفقیت انجام شد", data = createdUser)
            call.respond(HttpStatusCode.Created,responseMessage)


        }
        post("/refresh") {
            val refreshToken = call.request.header("Authorization")?.removePrefix("Bearer ")

            if (refreshToken.isNullOrEmpty()) {
                val responseModel = ResponseModel<User>(status = 400, isSuccessful = false,message = "رفرش توکن ارسال نشد",data = null)
                call.respond(HttpStatusCode.BadRequest, responseModel)
                return@post
            }

            // Validate the refresh token
            val decodedJWT = try {
                JWT.require(Algorithm.HMAC256("secret-key"))
                    .withIssuer("your.domain.com")
                    .build()
                    .verify(refreshToken)
            } catch (e: JWTVerificationException) {
                val responseModel = ResponseModel<User>(status = 400, isSuccessful = false,message = "توکن نا معتبر",data = null)
                call.respond(HttpStatusCode.Unauthorized, responseModel)
                return@post
            }

            val userId = decodedJWT.subject

            // Retrieve user and verify the token
            val usersCollection = DataBase.database.getCollection<User>("users")
            val user = usersCollection.find(Filters.eq("_id", userId)).toList().firstOrNull()

            if (user == null || user.refreshToken != refreshToken) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid refresh token")
                return@post
            }

            // Generate a new access token
            val newAccessToken = JwtConfig.generateToken(user.username, user.role)
            call.respond(HttpStatusCode.OK, mapOf("accessToken" to newAccessToken))
        }
    }

}

