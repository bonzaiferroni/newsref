package newsref.db.services

import newsref.db.DataService
import newsref.db.tables.*
import newsref.model.data.Source

class SourceService : DataService<Source, Long, SourceEntity>(
    SourceEntity,
    {source -> source.id},
    SourceEntity::fromData,
    SourceEntity::toData
) {

}