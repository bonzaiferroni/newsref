package newsref.db.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.db.serializers.UUIDSerializer
import newsref.model.dto.PrivateInfo
import java.util.*

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val name: String? = "",
    val username: String = "",
    val hashedPassword: String = "",
    val salt: String = "",
    val email: String? = null,
    val roles: String = "",
    val avatarUrl: String? = null,
    val venmo: String? = null,
    val createdAt: Instant = Instant.DISTANT_PAST,
    val updatedAt: Instant = Instant.DISTANT_PAST,
)

fun User.toPrivateInfo() = PrivateInfo(
    name = this.name,
    email = this.email,
)