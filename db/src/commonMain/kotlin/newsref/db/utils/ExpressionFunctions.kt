package newsref.db.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import newsref.db.utils.isNullOrEq
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.or
import kotlin.time.Duration

// fun Column<LocalDateTime>.less(instant: Instant): LessOp = this.less(instant.toLocalDateTimeUtc())

fun ExpressionWithColumnType<LocalDateTime>.since(duration: Duration) = duration.let { (Clock.System.now() - it).toLocalDateTimeUtc() }
    .let { Op.build { this@since.greater(it) } }

fun <T> ExpressionWithColumnType<T>.isNullOrEq(t: T) =
    Op.build { this@isNullOrEq.isNull() or this@isNullOrEq.eq(t) }

fun <T> ExpressionWithColumnType<T>.isNullOrNeq(t: T) =
    Op.build { this@isNullOrNeq.isNull() or this@isNullOrNeq.neq(t) }