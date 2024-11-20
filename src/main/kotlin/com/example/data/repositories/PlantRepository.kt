package com.example.data.repositories

import com.example.data.model.Plant
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

class PlantRepository(private val collection: CoroutineCollection<Plant>) {

    suspend fun getAllPlants(): List<Plant> = collection.find().toList()

    suspend fun getPlantById(id: String): Plant? =
        collection.findOne(Plant::id eq ObjectId(id))

    suspend fun addPlant(plant: Plant): Plant {
        collection.insertOne(plant)
        return plant
    }

    suspend fun updatePlant(id: String, updatedPlant: Plant): Boolean {
        val result = collection.updateOne(Plant::id eq ObjectId(id), updatedPlant)
        return result.modifiedCount > 0
    }

    suspend fun deletePlant(id: String): Boolean {
        val result = collection.deleteOne(Plant::id eq ObjectId(id))
        return result.deletedCount > 0
    }
}