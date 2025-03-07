package newsref.db.tables

import newsref.db.core.Aspect
import newsref.model.dto.HostDto
import org.jetbrains.exposed.sql.ResultRow

object HostDtoAspect : Aspect<HostDtoAspect, HostDto>(
    HostTable,
    ResultRow::toHostDto
) {
    val hostId = add(HostTable.id)
    val core = add(HostTable.core)
    val name = add(HostTable.name)
    val logo = add(HostTable.logo)
    val score = add(HostTable.score)
}

internal fun ResultRow.toHostDto() = HostDto(
    id = this[HostDtoAspect.hostId].value,
    core = this[HostDtoAspect.core],
    name = this[HostDtoAspect.name],
    logo = this[HostDtoAspect.logo],
    score = this[HostDtoAspect.score]
)