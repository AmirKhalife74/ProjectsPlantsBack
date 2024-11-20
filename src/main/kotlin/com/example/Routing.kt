package com.example

import com.example.data.model.Plant
import com.example.data.model.ResponseModel
import com.example.data.repositories.PlantRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val plants = mutableListOf<Plant>()
fun Application.configureRouting(repository: PlantRepository) {

    routing {
        get("/getAllPlants") {
            val plants =
                repository.getAllPlants() ?: return@get call.respondText(
                    "Plants not found",
                    status = HttpStatusCode.BadRequest
                )
            call.respond(plants)
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

            // Add plant to repository
            repository.addPlant(plant)

            // Create the response
            val response = ResponseModel(
                status = "200",
                message = "Plant added successfully" // Optionally include the plant in the response
            )

            // Respond with the status code and the serialized response body
            call.respond(HttpStatusCode.OK,response)
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

