package newsref.model.data

data class Link(
    val id: Long = 0,
    val sourceId: Long = 0,
    val url: String,
    val urlText: String,
)