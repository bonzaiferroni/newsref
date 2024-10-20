package newsref.db.tables

import newsref.model.data.Nexus
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object NexusTable : IntIdTable("nexus") {
	val name = text("name")
}

internal class NexusRow(id: EntityID<Int>) : IntEntity(id) {
	companion object : EntityClass<Int, NexusRow>(NexusTable)

	var name by NexusTable.name

	val hosts by HostRow optionalReferrersOn HostTable.nexusId
}

internal fun NexusRow.toData() = Nexus(
	id = this.id.value,
	name = this.name,
)

internal fun NexusRow.fromData(nexus: Nexus) {
	name = nexus.name
}