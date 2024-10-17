package newsref.server.plugins

import io.ktor.server.application.*
import newsref.db.initDb
import newsref.db.tables.UserRow
import newsref.db.tables.UserTable
import newsref.db.tables.fromData
import newsref.model.core.UserRole
import newsref.server.db.VariableStore
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Application.configureDatabases() {
    initDb()

    transaction {
        if (createAdmin()) println("created admin")
    }
}

fun createAdmin(): Boolean {
    val admin = VariableStore().admin
    val isAdmin = intParam(UserRole.ADMIN.ordinal) eq anyFrom(UserTable.roles)
    val adminRow = UserRow.find { isAdmin and (UserTable.username eq admin.username) }.firstOrNull()
    if (adminRow != null) return false
    UserRow.new { fromData(admin) }
    return true
}
