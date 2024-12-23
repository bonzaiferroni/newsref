package newsref.model.data

import newsref.model.core.Url

data class Feed(
    val id: Int,
    val url: Url,
    val selector: String,
    val external: Boolean,
)