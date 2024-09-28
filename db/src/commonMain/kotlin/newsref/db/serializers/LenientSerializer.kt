package newsref.db.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// A generic serializer that can handle any type T, falling back to a default value if deserialization fails.
class LenientSerializer<T : Any>(
    private val serializer: KSerializer<T>,
    private val defaultValue: T
) : KSerializer<T> {
    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeSerializableValue(serializer, value)
    }

    override fun deserialize(decoder: Decoder): T {
        return try {
            decoder.decodeSerializableValue(serializer) // Attempt to deserialize the value as type T
        } catch (e: SerializationException) {
            println("Failed to deserialize property '${descriptor.serialName}', using default value: ${e.message}")
            defaultValue // Return default value if deserialization fails
        }
    }
}