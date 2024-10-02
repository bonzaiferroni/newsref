package newsref.db.utils

import newsref.model.core.toCheckedUrl

internal fun String.toCheckedFromDb() = this.toCheckedUrl(emptySet(), emptySet())