package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.ObjectIdSerializer
import com.example.data.repositories.PlantRepository
import com.example.data.repositories.UserRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
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
}

fun Application.configureMongoDB() {
    mongoClient = KMongo.createClient().coroutine
    database = mongoClient.getDatabase("plantsDB") // نام دیتابیس
    log.info("Connected to MongoDB!")
}

fun Application.module() {
    configureMongoDB()
    configureSecurity()
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
    configureRouting(plantRepository,userRepository)

}

fun Application.configureSecurity()
{
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "projectplantsRealm"
            verifier(
                JWT.require(Algorithm.HMAC256("your-secret-key"))
                    .withIssuer("projectplants")
                    .withAudience("projectplantsAudience")
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
