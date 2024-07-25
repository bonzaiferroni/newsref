package streetlight.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ImageUploadRequest(
    val eventId: Int,
    val image: String,
    val filename: String,
)