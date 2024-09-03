package streetlight.web.io

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Encoder

class MySerializer<T>(override val descriptor: SerialDescriptor) : SerializationStrategy<T> {
    override fun serialize(encoder: Encoder, value: T) {
        TODO("Not yet implemented")
    }
}