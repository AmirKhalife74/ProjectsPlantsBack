package com.example.routing

import com.example.data.model.auth.LoginRequest
import com.example.data.repositories.PlantRepository
import com.example.data.repositories.UserRepository
import com.example.utils.JwtConfig.generateAdminToken
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configurePanelRouting(plantRepository: PlantRepository,userRepository: UserRepository)
{
    routing {
        post("/admin/login") {
            val credentials = call.receive<LoginRequest>()
            if (credentials.username == "admin" && credentials.password == "1234") {
                val token = generateAdminToken(credentials.username)
                call.respond(mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }
        authenticate("auth-admin") {

            // Panel Main Page Data
            get("admin/getAllPlants") {
                val plants = plantRepository.getAllPlants()
                call.respond(HttpStatusCode.OK,plants)
            }

            get("admin/getAllUsers") {
                val users = userRepository.getAllUsers()
                call.respond(HttpStatusCode.OK,users)
            }
        }
    }
}