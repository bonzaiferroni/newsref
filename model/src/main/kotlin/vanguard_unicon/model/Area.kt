package vanguard_unicon.model

import kotlinx.serialization.Serializable

@Serializable
data class Area(
    val id: Int,
    val name: String,
    val parentId: Int?
)