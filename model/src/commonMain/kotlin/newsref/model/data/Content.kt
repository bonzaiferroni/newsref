package newsref.model.data

data class Content(
    val id: Long = 0,
    val sourceId: Long = 0,
    val outletId: Long = 0,
    val tag: String = "",
    val text: String = "",
)