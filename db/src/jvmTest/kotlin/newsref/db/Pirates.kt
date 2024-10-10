package newsref.db

import org.jetbrains.exposed.sql.Table

object Pirates : Table() {
	val id = integer("id").autoIncrement()
	val name = varchar("name", 50)
	val bounty = integer("bounty")

	override val primaryKey = PrimaryKey(id)
}