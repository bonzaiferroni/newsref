package newsref.db.utils

import newsref.model.core.SortDirection
import org.jetbrains.exposed.sql.SortOrder

fun SortDirection.toSortOrder() = when (this) {
    SortDirection.Ascending -> SortOrder.ASC_NULLS_LAST
    SortDirection.Descending -> SortOrder.DESC_NULLS_LAST
}