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
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting(repository: PlantRepository) {

    routing {
//        intercept(Plugins) {
//            val principal = call.principal<JWTPrincipal>()
//            if (principal == null) {
//                call.respond(HttpStatusCode.Unauthorized, "Please log in")
//                return@intercept finish()  // درخواست متوقف می‌شود و پیغام خطا می‌دهد
//            }
//
//            val role = principal.getClaim("role", String::class)
//            if (role != "admin") {
//                call.respond(HttpStatusCode.Forbidden, "Access Denied")
//                return@intercept finish()  // دسترسی به روت‌های admin فقط در صورتی که نقش admin باشد
//            }
//        }

        authenticate("auth-user") {
            route("/app") {
                get("/getAllPlants") {
                    val plants =
                        repository.getAllPlants()
                    if (plants.isNotEmpty()) {
                        val response: ResponseModel<List<Plant>> =ResponseModel(data = plants, status = 200, isSuccessful = true, message = "عملیات با موفقیت انجام شد")
                        call.respond(HttpStatusCode.OK,response )
                    } else {
                        call.respond(HttpStatusCode.NotFound, "There is no plants")
                    }

                }
                post("/getPlantById{id}") {
                    val id =
                        call.parameters["id"] ?: return@post call.respondText(
                            "Bad Request",
                            status = HttpStatusCode.BadRequeKst
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

                }

            }
        }

    }
}

