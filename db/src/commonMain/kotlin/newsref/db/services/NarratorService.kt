package newsref.db.services

import newsref.db.*
import newsref.db.tables.*
import newsref.db.model.Narrator
import org.jetbrains.exposed.sql.*

class NarratorService : DbService() {
    suspend fun insertNarrator(narrator: Narrator) = dbQuery {
        NarratorTable.insertAndGetId {
            it[vectorModelId] = narrator.vectorModelId
            it[name] = narrator.name
            it[bio] = narrator.bio
        }.value
    }

    suspend fun readNarrator(narratorId: Int) = dbQuery {
        NarratorTable.select(NarratorTable.columns)
            .where { NarratorTable.id eq narratorId }
            .firstOrNull()?.toNarrator()
    }
}