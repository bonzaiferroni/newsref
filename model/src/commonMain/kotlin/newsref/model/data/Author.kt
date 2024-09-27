package newsref.model.data

data class Author(
    val id: Int = 0,
    val bylines: Set<String> = emptySet()
)