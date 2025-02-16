package newsref.db.model

data class Author(
    val id: Int = 0,
    val name: String,
    val url: String?,
    val bylines: Set<String> = emptySet()
)