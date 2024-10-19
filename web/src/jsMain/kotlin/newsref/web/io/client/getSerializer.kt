package newsref.web.io.client

import kotlinx.serialization.SerializationStrategy
import newsref.model.dto.*

inline fun <reified T> getSerializer(): SerializationStrategy<T> {
    @Suppress("UNCHECKED_CAST")
    return when (T::class) {
        // dto
        AuthInfo::class -> AuthInfo.serializer() as SerializationStrategy<T>
        ImageUploadRequest::class -> ImageUploadRequest.serializer() as SerializationStrategy<T>
        PrivateInfo::class -> PrivateInfo.serializer() as SerializationStrategy<T>
        SourceInfo::class -> SourceInfo.serializer() as SerializationStrategy<T>
        // models
        LoginRequest::class -> LoginRequest.serializer() as SerializationStrategy<T>
        SignUpRequest::class -> SignUpRequest.serializer() as SerializationStrategy<T>
        EditUserRequest::class -> EditUserRequest.serializer() as SerializationStrategy<T>
        else -> throw IllegalArgumentException("No serializer for ${T::class}")
    }
}