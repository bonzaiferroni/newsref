package streetlight.model.core

import kotlinx.serialization.Serializable

@Serializable
data class Contact(
    override val id: Int = 0,
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val socialMedia: String = "",
): IdModel