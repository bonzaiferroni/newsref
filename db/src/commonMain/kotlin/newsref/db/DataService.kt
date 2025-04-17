package newsref.db

import klutch.db.DbService
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass

abstract class DataService<Data, IdType: Comparable<IdType>, DataEntity: Entity<IdType>>(
    private val entity: EntityClass<IdType, DataEntity>,
    private val provideId: (Data) -> IdType,
    private val fromObj: DataEntity.(Data) -> Unit,
    private val toObj: DataEntity.() -> Data,
): DbService() {
    suspend fun create(data: Data): IdType = dbQuery {
        entity.new { fromObj(data) }.id.value
    }

    suspend fun read(id: IdType): Data? = dbQuery {
        entity.findById(id)?.toObj()
    }

    suspend fun readAll(): List<Data> = dbQuery {
        entity.all().map { it.toObj() }
    }

    suspend fun update(data: Data): Data = dbQuery {
        val id = provideId(data)
        val updatedData = entity.findByIdAndUpdate(id) { fromObj(it, data) }
            ?: throw IllegalArgumentException("Not found")
        updatedData.toObj()
    }

    suspend fun delete(id: IdType) = dbQuery {
        entity.findById(id)?.delete()
    }
}
