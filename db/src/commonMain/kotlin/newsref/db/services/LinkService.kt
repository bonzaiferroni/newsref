package newsref.db.services

import newsref.db.DataService
import newsref.db.tables.LinkEntity
import newsref.db.tables.fromData
import newsref.db.tables.toData
import newsref.model.data.Link

class LinkService: DataService<Link, Long, LinkEntity>(
    LinkEntity,
    {link -> link.id},
    LinkEntity::fromData,
    LinkEntity::toData
) {

}