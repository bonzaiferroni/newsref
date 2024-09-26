package newsref.model.data

data class Author(
    val id: Int = 0,
    val byLines: Set<String> = emptySet()
)