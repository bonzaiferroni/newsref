package newsref.db.tables

import klutch.db.Aspect
import newsref.model.data.Host
import org.jetbrains.exposed.sql.ResultRow

object HostDtoAspect : Aspect<HostDtoAspect, Host>(
    HostTable,
    ResultRow::toHostDto
) {
    val hostId = add(HostTable.id)
    val core = add(HostTable.core)
    val name = add(HostTable.name)
    val logo = add(HostTable.logo)
    val score = add(HostTable.score)
}

internal fun ResultRow.toHostDto() = Host(
    id = this[HostDtoAspect.hostId].value,
    core = this[HostDtoAspect.core],
    name = this[HostDtoAspect.name],
    logo = this[HostDtoAspect.logo],
    score = this[HostDtoAspect.score]
)