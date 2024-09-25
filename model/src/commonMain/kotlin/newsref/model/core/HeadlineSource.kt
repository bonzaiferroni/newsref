package newsref.model.core

data class HeadlineSource(
    override val id: Int,
    val url: String
): IdModel