package streetlight.web.io

import kotlinx.serialization.SerializationStrategy
import streetlight.model.Area
import streetlight.model.User

inline fun <reified T> getSerializer(): SerializationStrategy<T> {
    @Suppress("UNCHECKED_CAST")
    return when (T::class) {
        User::class -> User.serializer() as SerializationStrategy<T>
        Area::class -> Area.serializer() as SerializationStrategy<T>
        else -> throw IllegalArgumentException("No serializer for ${T::class}")
    }
}