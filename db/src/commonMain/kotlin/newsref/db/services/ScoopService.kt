package newsref.db.services

import newsref.db.DataService
import newsref.db.tables.ScoopRow
import newsref.db.tables.fromData
import newsref.db.tables.toData
import newsref.model.data.Scoop

class ScoopService : DataService<Scoop, Long, ScoopRow>(
    ScoopRow,
    {scoop -> scoop.id },
    ScoopRow::fromData,
    ScoopRow::toData,
) {
}