package com.example

import com.example.data.model.Plant
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
val plants = mutableListOf<Plant>()
fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/plants") {
            val plants =
                getAllPlants() ?: return@get call.respondText("Plants not found", status = HttpStatusCode.BadRequest)
            call.respond(plants)
        }
        post("/plant{id}") {
            val id =
                call.parameters["id"] ?: return@post call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
            val plant = getPlantById(id.toInt())?: return@post call.respondText("PlantNot found", status = HttpStatusCode.NotFound)
            call.respond(plant)
        }

        post("/addPlant"){
            val plant = call.receive<Plant>()
            plants.add(plant)
            call.respondText("plant added successfully", status = HttpStatusCode.OK)
        }

        put("/editPlant{id}")
        {
            val id = call.parameters["id"] ?: return@put call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
            val updatedPlant = call.receive<Plant>()
            val index = plants.indexOfFirst { it.id == id.toInt() }
            if (index != -1) {
                plants[index] = updatedPlant
                call.respondText("Plant updated successfully!")
            } else {
                call.respondText("Plant not found!", status = HttpStatusCode.NotFound)
            }

        }

        delete("/deletePlant{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val removed = plants.removeIf { it.id == id.toInt() }
            if (removed) {
                call.respondText("Plant removed successfully!", status = HttpStatusCode.OK)
            }else
            {
                call.respondText("Plant not found!", status = HttpStatusCode.NotFound)
            }
        }
    }
}

fun getAllPlants(): List<Plant> {
    return plants
}

fun getPlantById(id: Int): Plant? {
    val plant = plants.find { it.id == id }
    return plant
}

