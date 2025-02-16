package newsref.db.model

import newsref.db.core.Url

data class Scoop(
    val id: Long = 0,
    val url: Url,
    val html: String = "",
)