package com.example

import com.example.data.model.auth.LoginRequest
import com.example.data.model.Plant
import com.example.data.model.auth.RegisterRequest
import com.example.data.model.ResponseModel
import com.example.data.model.User
import com.example.data.repositories.PlantRepository
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


fun Application.configureRouting(repository: PlantRepository,userRepository: UserRepository) {

    routing {


        get("/")
        {
            call.respond("Hello World!")
        }
        get("/getAllPlants") {
            val plants =
                repository.getAllPlants() ?: return@get call.respondText(
                    "Plants not found",
                    status = HttpStatusCode.BadRequest
                )
            if (plants.size!=0) {
                call.respond(plants)
            }else
            {
                call.respond(HttpStatusCode.NotFound,"There is no plants")
            }

        }
        post("/getPlantById{id}") {
            val id =
                call.parameters["id"] ?: return@post call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
            val plant = repository.getPlantById(id) ?: return@post call.respondText(
                "PlantNot found",
                status = HttpStatusCode.NotFound
            )
            call.respond(plant)
        }

        post("/addPlant") {
            val plant = call.receive<Plant>()
            try {
                repository.addPlant(plant)
                val response = ResponseModel(
                    status = "200",
                    message = "Plant added successfully"

                )
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        put("/editPlantById{id}")
        {
            val id = call.parameters["id"]
            val updatedPlant = call.receive<Plant>()
            val isUpdated = id?.let { repository.updatePlant(it, updatedPlant) } ?: false
            if (isUpdated) {
                call.respondText("Plant updated successfully")
            } else {
                call.respondText("Failed to update plant", status = HttpStatusCode.BadRequest)
            }

        }

        delete("/deletePlant{id}") {
            val id = call.parameters["id"]
            val isDeleted = id?.let { repository.deletePlant(it) } ?: false
            if (isDeleted) {
                call.respondText("Plant deleted successfully")
            } else {
                call.respondText("Failed to delete plant", status = HttpStatusCode.BadRequest)
            }
        }

        post("/login") {

            val loginRequest = call.receive<LoginRequest>()
            val user = userRepository.getUserByUsername(loginRequest.username)

            if (user != null && userRepository.verifyPassword(loginRequest.password, user.passwordHash)) {
                val token = JwtConfig.generateToken(user.username,user.role)
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
            val createdUser = userRepository.createUser(username = registerRequest.username, password = registerRequest.password,registerRequest.email,role)

            call.respond(HttpStatusCode.Created, createdUser)

        }
        authenticate("auth-jwt") {
            get("/getAllUsers") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal?.payload?.getClaim("username")?.asString()
                val role = principal?.payload?.getClaim("role")?.asString()

                call.respondText("Hello, $username with role $role")
            }
        }

    }
}

