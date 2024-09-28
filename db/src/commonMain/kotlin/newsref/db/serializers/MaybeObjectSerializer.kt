package newsref.db.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import newsref.db.models.QuantitativeValue

open class MaybeObjectSerializer<Primitive : Any, Object : Any>(
    private val objectSerializer: KSerializer<Object>,
    private val primitiveSerializer: KSerializer<Primitive>,
    private val primitiveToObject: (Primitive) -> Object
) : KSerializer<Object> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("LiteralOrObject") {
        element("object", objectSerializer.descriptor)
        element("primitive", primitiveSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: Object) {
        encoder.encodeSerializableValue(objectSerializer, value)
    }

    override fun deserialize(decoder: Decoder): Object {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("This serializer only works with Json format")
        return when (val jsonElement = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> {
                jsonDecoder.json.decodeFromJsonElement(primitiveSerializer, jsonElement).let(primitiveToObject)
            }
            is JsonObject -> {
                jsonDecoder.json.decodeFromJsonElement(objectSerializer, jsonElement)
            }
            else -> throw SerializationException("Unexpected JSON element")
        }
    }
}

object QuantitativeValueSerializer : MaybeObjectSerializer<Int, QuantitativeValue>(
    objectSerializer = QuantitativeValue.serializer(),
    primitiveSerializer = Int.serializer(),
    primitiveToObject = { QuantitativeValue(value = it) }
)