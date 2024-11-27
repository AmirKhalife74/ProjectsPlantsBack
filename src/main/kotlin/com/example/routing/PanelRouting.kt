package com.example.routing

import com.example.data.model.Plant
import com.example.data.model.ResponseModel
import com.example.data.model.auth.LoginRequest
import com.example.data.repositories.PlantRepository
import com.example.data.repositories.UserRepository
import com.example.utils.JwtConfig.generateAdminToken
import com.example.utils.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configurePanelRouting(plantRepository: PlantRepository, userRepository: UserRepository) {
    routing {
        post("/admin/login") {
            val credentials = call.receive<LoginRequest>()
            if (credentials.username == "admin") {
                val token = generateAdminToken(credentials.username, UserRole.ADMIN)
                call.respond(mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }
        authenticate("auth-admin") {
            route("/admin") {
                // Panel Main Page Data
                post("/getAllPlants") {
                    val authHeader = call.request.headers["Authorization"]
                    println("Authorization Header: $authHeader") // Debug log
                    val plants = plantRepository.getAllPlants()
                    call.respond(HttpStatusCode.OK, plants)
                }
                get("/getAllUsers") {
                    val users = userRepository.getAllUsers()
                    call.respond(HttpStatusCode.OK, users)
                }
                post("/addPlant") {
                    val plant = call.receive<Plant>()
                    try {
                        plantRepository.addPlant(plant)
                        val response = ResponseModel<String>(
                            status = 200,
                            message = "Plant added successfully",
                            isSuccessful = true,
                            data = ""
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
                    val isUpdated = id?.let { plantRepository.updatePlant(it, updatedPlant) } ?: false
                    if (isUpdated) {
                        call.respondText("Plant updated successfully")
                    } else {
                        call.respondText("Failed to update plant", status = HttpStatusCode.BadRequest)
                    }

                }

                delete("/deletePlant{id}") {
                    val id = call.parameters["id"]
                    val isDeleted = id?.let { plantRepository.deletePlant(it) } ?: false
                    if (isDeleted) {
                        call.respondText("Plant deleted successfully")
                    } else {
                        call.respondText("Failed to delete plant", status = HttpStatusCode.BadRequest)
                    }
                }
            }
        }
    }
}