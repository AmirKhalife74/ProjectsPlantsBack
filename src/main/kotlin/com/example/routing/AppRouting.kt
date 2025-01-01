package com.example.routing

import com.example.data.model.ResponseModel
import com.example.data.repositories.PlantRepository
import com.example.data.repositories.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting(repository: PlantRepository,userRepository: UserRepository) {

    routing {
        get("/getAllPlants") {
            val plants =
                repository.getAllPlants()
            if (plants.isNotEmpty()) {
                val responseModel = ResponseModel(
                    status = 200,
                    isSuccessful = true,
                    message = "عملیات با موفقیت انحام شذ",
                    data = plants
                )
                call.respond(HttpStatusCode.OK, responseModel)
            } else {
                call.respond(HttpStatusCode.NotFound, "There is no plants")
            }

        }
        authenticate("auth-user") {
            route("/app") {
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
                post("/getUserInfo") {
                    val id = call.parameters["id"] ?: return@post call.respondText(
                        "not found",
                        status = HttpStatusCode.BadRequest
                    )
//                    val user = userRepository.getUserById(id)
//                    if (user == null) {
//                        call.respond(HttpStatusCode.OK, user)
//                    }else {
//                        call.respond(HttpStatusCode.NotFound, "Not found")
//                    }

                }

            }
        }

    }
}

