package newsref.db

import org.jetbrains.exposed.sql.Transaction
import klutch.db.tables.UserTable
import klutch.db.tables.toUser
import newsref.db.utils.readFirstOrNull

internal fun Transaction.findUser(username: String) = UserTable.readFirstOrNull { it.username.eq(username) }?.toUser()

internal fun Transaction.findUserIdOrThrow(username: String) = UserTable
    .select(UserTable.id)
    .where { UserTable.username eq username }
    .map { it[UserTable.id] }
    .firstOrNull() ?: throw IllegalArgumentException("User not found")