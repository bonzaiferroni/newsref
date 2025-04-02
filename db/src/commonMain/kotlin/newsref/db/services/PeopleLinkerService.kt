package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.PagePersonTable
import newsref.db.tables.PersonTable
import newsref.db.tables.toPerson
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update

class PeopleLinkerService: DbService() {


    suspend fun readPeopleWithName(name: String) = dbQuery {
        PersonTable.select(PersonTable.columns)
            .where { PersonTable.name.eq(name) }
            .map { it.toPerson() }
    }

    suspend fun linkPerson(pageId: Long, personId: Int) = dbQuery {
        PagePersonTable.insert {
            it[this.pageId] = pageId
            it[this.personId] = personId
        }
    }

    suspend fun createPerson(name: String, identifier: String) = dbQuery {
        PersonTable.insertAndGetId {
            it[this.name] = name
            it[this.identifiers] = listOf(identifier)
        }.value
    }

    suspend fun addIdentifier(personId: Int, identifier: String) = dbQuery {
        val identifiers = PersonTable.select(PersonTable.identifiers)
            .where { PersonTable.id.eq(personId) }
            .firstOrNull()?.let { it[PersonTable.identifiers] } ?: emptyList()
        PersonTable.update({ PersonTable.id.eq(personId) }) {
            it[this.identifiers] = identifiers + identifier
        }
    }
}