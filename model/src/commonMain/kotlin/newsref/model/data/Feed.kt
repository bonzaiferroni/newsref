package newsref.model.data

import com.eygraber.uri.Url

data class Feed(
    val id: Int,
    val url: Url,
    val selector: String,
)