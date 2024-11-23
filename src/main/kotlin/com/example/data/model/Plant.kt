package com.example.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Plant(
    @Serializable(with = ObjectIdSerializer::class) val id: ObjectId? = ObjectId(),
    val name: String,
    val description: String,
    val careInstructions: CareInstructions,
    val categories: List<String>,
    val price: Double,
    val stock: Int,
    val images: List<String>
)
@Serializable
data class CareInstructions(
    val watering: String,
    val sunlight: String
)

object ObjectIdSerializer : KSerializer<ObjectId> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ObjectId", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ObjectId) {
        encoder.encodeString(value.toHexString())
    }

    override fun deserialize(decoder: Decoder): ObjectId {
        return ObjectId(decoder.decodeString())
    }
}
