package newsref.db.utils

import newsref.model.core.parseChecked

internal fun String.toCheckedFromDb() = this.parseChecked(emptySet(), emptySet())