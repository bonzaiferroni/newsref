package streetlight.web.io

import kotlinx.serialization.SerializationStrategy
import streetlight.model.core.*
import streetlight.model.dto.*

inline fun <reified T> getSerializer(): SerializationStrategy<T> {
    @Suppress("UNCHECKED_CAST")
    return when (T::class) {
        // dto
        AuthInfo::class -> AuthInfo.serializer() as SerializationStrategy<T>
        EventInfo::class -> EventInfo.serializer() as SerializationStrategy<T>
        ImageUploadRequest::class -> ImageUploadRequest.serializer() as SerializationStrategy<T>
        // models
        Area::class -> Area.serializer() as SerializationStrategy<T>
        Event::class -> Event.serializer() as SerializationStrategy<T>
        Location::class -> Location.serializer() as SerializationStrategy<T>
        Request::class -> Request.serializer() as SerializationStrategy<T>
        Song::class -> Song.serializer() as SerializationStrategy<T>
        LoginRequest::class -> LoginRequest.serializer() as SerializationStrategy<T>
        SignUpRequest::class -> SignUpRequest.serializer() as SerializationStrategy<T>
        EditUserRequest::class -> EditUserRequest.serializer() as SerializationStrategy<T>
        PrivateInfo::class -> PrivateInfo.serializer() as SerializationStrategy<T>
        else -> throw IllegalArgumentException("No serializer for ${T::class}")
    }
}