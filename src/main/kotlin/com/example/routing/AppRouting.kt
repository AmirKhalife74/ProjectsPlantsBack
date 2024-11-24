package com.example.routing

import com.example.data.model.auth.LoginRequest
import com.example.data.model.Plant
import com.example.data.model.auth.RegisterRequest
import com.example.data.model.ResponseModel
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


fun Application.configureRouting(repository: PlantRepository) {

    routing {

        authenticate("auth-user") {


            get("/getAllPlants") {
                val plants =
                    repository.getAllPlants()
                if (plants.isNotEmpty()) {
                    call.respond(HttpStatusCode.OK,plants)
                } else {
                    call.respond(HttpStatusCode.NotFound, "There is no plants")
                }

            }
            post("/getPlantById{id}") {
                val id =
                    call.parameters["id"] ?: return@post call.respondText(
                        "Bad Request",
                        status = HttpStatusCode.BadRequest
                    )
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
        }

    }
}

