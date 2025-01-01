package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.ObjectIdSerializer
import com.example.data.repositories.PlantRepository
import com.example.data.repositories.UserRepository
import com.example.routing.configureAuthRouting
import com.example.routing.configurePanelRouting
import com.example.routing.configureRouting
import com.example.utils.UserRole
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


lateinit var mongoClient: CoroutineClient
lateinit var database: CoroutineDatabase
fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module() // Calls the module function where your routes and configurations are set up
    }.start(wait = true)
}

fun Application.configureMongoDB() {
    mongoClient = KMongo.createClient().coroutine
    database = mongoClient.getDatabase("plantsDB") // نام دیتابیس
    log.info("Connected to MongoDB!")
}

fun Application.module() {
    configureMongoDB()
    configureLogging()
    install(ContentNegotiation)
    {

        json(Json {
            prettyPrint = true
            isLenient = true
            serializersModule = SerializersModule {
                contextual(ObjectId::class, ObjectIdSerializer)
            }
        })
    }
    val plantRepository = PlantRepository(database.getCollection("plants"))
    val userRepository = UserRepository(database.getCollection("users"))
    configureAuthRouting(userRepository)

    routing {
        route("/api") {
           // configureStatusPages()
            configureSecurity()
            configureRouting(plantRepository,userRepository)
            configurePanelRouting(plantRepository,userRepository)

        }
    }

}

fun Application.configureSecurity()
{
    install(Authentication) {
        jwt("auth-user") {
            realm = "AppRealm"
            verifier(JWT.require(Algorithm.HMAC256("your-secret-key"))
                .withIssuer("projectPlants")
                .build()
            )
            validate { credential ->
                val role = credential.payload.getClaim("role").asString()
                if (role == "USER") JWTPrincipal(credential.payload) else null
            }
        }

        jwt("auth-admin") {
            realm = "AppRealm"
            verifier(JWT.require(Algorithm.HMAC256("your-secret-key"))
                .withIssuer("projectPlants")
                .build()
            )
            validate { credential ->
                val role = credential.payload.getClaim("role").asString()
                println("Role in token: $role") // Debug log
                if (role == "ADMIN" || role == "MODERATOR") JWTPrincipal(credential.payload) else null
            }
        }
    }
}

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.Unauthorized) { call, _ ->
            call.respond(HttpStatusCode.Unauthorized, "Unauthorized access")
        }

        status(HttpStatusCode.Forbidden) { call, _ ->
            call.respond(HttpStatusCode.Forbidden, "Forbidden access")
        }
    }
}

fun Application.configureLogging() {
    intercept(ApplicationCallPipeline.Monitoring) {
        val method = call.request.httpMethod.value
        val uri = call.request.uri
        println("Received request: $method $uri")
    }
}
