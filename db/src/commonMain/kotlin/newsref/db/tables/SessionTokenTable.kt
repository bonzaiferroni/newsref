package newsref.db.tables

import newsref.db.model.SessionToken
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object SessionTokenTable : LongIdTable() {
    val user = reference("user_id", UserTable, onDelete = ReferenceOption.CASCADE)
    val token = text("token")
    val createdAt = long("created_at")
    val expiresAt = long("expires_at")
    val issuer = text("issuer")
}

class SessionTokenEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SessionTokenEntity>(SessionTokenTable)

    var user by UserRow referencedOn SessionTokenTable.user
    var token by SessionTokenTable.token
    var createdAt by SessionTokenTable.createdAt
    var expiresAt by SessionTokenTable.expiresAt
    var issuer by SessionTokenTable.issuer
}

fun SessionTokenEntity.toModel() = SessionToken(
    this.id.value,
    this.user.id.value,
    this.token,
    this.createdAt,
    this.expiresAt,
    this.issuer,
)

fun SessionTokenEntity.fromModel(data: SessionToken) {
    user = UserRow[data.userId]
    token = data.token
    createdAt = data.createdAt
    expiresAt = data.expiresAt
    issuer = data.issuer
}