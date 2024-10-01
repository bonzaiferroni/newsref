package newsref.model.data

import com.eygraber.uri.Uri
import com.eygraber.uri.Url

data class Scoop(
    val id: Long = 0,
    val url: Url,
    val html: String = "",
)