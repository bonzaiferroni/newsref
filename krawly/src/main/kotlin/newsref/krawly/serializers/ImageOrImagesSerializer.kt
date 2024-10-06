package newsref.krawly.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

class SingleOrArraySerializer<Target : Any>(private val serializer: KSerializer<Target>) : KSerializer<List<Target>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SingleOrArray") {
        element<JsonElement>("element")
    }

    override fun serialize(encoder: Encoder, value: List<Target>) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("This class can be saved only by Json")
        val jsonElement = if (value.size == 1) {
            jsonEncoder.json.encodeToJsonElement(serializer, value[0])
        } else {
            jsonEncoder.json.encodeToJsonElement(ListSerializer(serializer), value)
        }
        encoder.encodeJsonElement(jsonElement)
    }

    override fun deserialize(decoder: Decoder): List<Target> {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("This class can be loaded only by Json")
        return when (val jsonElement = jsonDecoder.decodeJsonElement()) {
            is JsonArray -> jsonDecoder.json.decodeFromJsonElement(ListSerializer(serializer), jsonElement)
            is JsonObject -> listOf(jsonDecoder.json.decodeFromJsonElement(serializer, jsonElement))
            else -> throw SerializationException("Unexpected JSON element")
        }
    }
}