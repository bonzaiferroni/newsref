package newsref.model.data

import newsref.model.core.Url

data class Scoop(
    val id: Long = 0,
    val url: Url,
    val html: String = "",
)