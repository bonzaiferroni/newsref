package newsref.db.model

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val id: Int = 0,
    val name: String,
    val identifier: String
)