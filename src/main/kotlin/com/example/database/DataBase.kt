package com.example.database
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.reactivestreams.KMongo

object DataBase {
    private val client = KMongo.createClient().coroutine
    val database: CoroutineDatabase = client.getDatabase("plantsapp")
}