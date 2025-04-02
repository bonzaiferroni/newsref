package newsref.krawly.agents

import newsref.db.globalConsole
import newsref.db.model.Page
import newsref.db.model.Person
import newsref.db.services.PERSON_UNCLEAR
import newsref.db.services.PeopleLinkerService
import newsref.krawly.clients.GeminiClient
import newsref.krawly.clients.promptTemplate

private val console = globalConsole.getHandle(PeopleLinkerClient::class)

class PeopleLinkerClient(
    val client: GeminiClient,
    val service: PeopleLinkerService = PeopleLinkerService(),
) {
    suspend fun linkPeople(page: Page, peopleResponses: List<String>, articleContent: String) {
        val people = peopleResponses.map {
            val split = it.split(":")
            split[0] to split.getOrNull(1)
        }

        val records = mutableSetOf<Pair<Int, String>>()
        val possibilities = mutableListOf<Person>()
        for ((name, identifier) in people) {
            var personId: Int? = null

            val peopleWithName = service.readPeopleWithName(name)
            if (identifier == null || identifier == PERSON_UNCLEAR) {
                if (peopleWithName.size == 1) {
                    personId = peopleWithName.first().id
                }
                continue
            }

            personId = peopleWithName.firstOrNull {
                it.identifiers.map{ it.lowercase()}.contains(identifier.lowercase())
            }?.id

            if (personId == null) {
                if (peopleWithName.isNotEmpty()) {
                    possibilities.addAll(peopleWithName)
                }
            }

            if (personId != null) {
                records.add(personId to name)
            }
        }


        if (possibilities.isNotEmpty()) {
            val clarifyingPrompt = promptTemplate(
                "../docs/article_reader-clarify_person.md",
                "person_table" to createPersonTable(possibilities),
                "article_content" to articleContent,
            )

            val response: PersonChoiceResponse? = client.requestJson(2, clarifyingPrompt)
            if (response == null) {
                console.logError("unable to clarify, received no response")
            } else {
                val pairs = response.people.mapNotNull {
                    val split = it.split(":")
                    if (split.size != 2) return@mapNotNull null
                    val id = split[0].toIntOrNull()
                    val name = split[1]
                    if (
                        id == null ||
                        !possibilities.any { it.id == id } ||
                        !possibilities.any { it.name == name }
                    ) return@mapNotNull null
                    id to name
                }
                for ((id, name) in pairs) {
                    if (pairs.count { it.second == name } != 1) continue
                    val identifier = people.firstOrNull { it.first.lowercase() == name.lowercase() }?.second
                    if (identifier == null) continue
                    console.log("Clarified person: $name ($identifier)")
                    service.addIdentifier(id, identifier)
                    records.add(id to name)
                }
            }
        }

        for ((name, identifier) in people) {
            if (identifier == null || records.any { it.second == name }) continue
            console.log("Created person: $name ($identifier)")
            val personId = service.createPerson(name, identifier)
            records.add(personId to name)
        }

        for (personId in records.map { it.first }.toSet()) {
            service.linkPerson(page.id, personId)
        }
        console.log("Linked ${records.size} people to the page")
    }

    private fun createPersonTable(people: List<Person>) = buildString {
        append("|id|name|title|\n")
        append("|---|---|---|\n")
        for (person in people) {
            append("|${person.id}|${person.name}|${person.identifiers.joinToString(", ")}|\n")
        }
    }
}