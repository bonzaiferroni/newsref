package newsref.db.tables

import newsref.db.model.Nexus
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

internal object NexusTable : IntIdTable("nexus") {
	val name = text("name")
}

internal fun ResultRow.toNexus() = Nexus(
	id = this[NexusTable.id].value,
	name = this[NexusTable.name],
)