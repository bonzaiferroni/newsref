package newsref.db.services

import newsref.db.DataService
import newsref.db.tables.ScoopRow
import newsref.db.tables.fromModel
import newsref.db.tables.toModel
import newsref.db.model.Scoop

class ScoopService : DataService<Scoop, Long, ScoopRow>(
    ScoopRow,
    {scoop -> scoop.id },
    ScoopRow::fromModel,
    ScoopRow::toModel,
) {
}