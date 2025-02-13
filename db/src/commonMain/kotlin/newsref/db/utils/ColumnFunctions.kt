package newsref.db.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.LessOp

// fun Column<LocalDateTime>.less(instant: Instant): LessOp = this.less(instant.toLocalDateTimeUtc())