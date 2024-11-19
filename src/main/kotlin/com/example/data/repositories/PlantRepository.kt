package com.example.data.repositories

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

class PlantRepository {

    object Database {
        // Connecting to the db
        private val client = KMongo.createClient("mongodb+srv://<username>:<password>@cluster0.mongodb.net/?retryWrites=true&w=majority").coroutine
        val database: CoroutineDatabase = client.getDatabase("plantsapp") // database name
    }
}