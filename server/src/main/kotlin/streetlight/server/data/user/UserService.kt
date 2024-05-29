package streetlight.server.data.user

import streetlight.model.User
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import streetlight.server.data.ApiService

class UserService(database: Database) : ApiService(database, UserTable) {

    suspend fun create(user: User): Int = dbQuery {
        UserEntity.new {
            name = user.name
            email = user.email
            password = user.password
        }.id.value
    }

    suspend fun read(id: Int): User? {
        return dbQuery {
            UserEntity.findById(id)
                ?.let {
                    User(
                        it.id.value,
                        it.name,
                        it.email,
                        it.password
                    )
                }
        }
    }

    suspend fun update(id: Int, user: User) {
        dbQuery {
            UserEntity.findById(id)?.let {
                it.name = user.name
                it.email = user.email
                it.password = user.password
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            UserEntity.findById(id)?.delete()
        }
    }
}

