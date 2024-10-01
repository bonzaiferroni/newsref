package newsref.model.data

import com.eygraber.uri.Url

data class Link(
    val id: Long = 0,
    val sourceId: Long = 0,
    // todo: add content id
    val url: Url,
    val text: String,
)