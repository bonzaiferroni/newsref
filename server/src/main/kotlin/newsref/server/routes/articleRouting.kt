package newsref.server.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.db.services.ArticleDtoService
import newsref.db.services.SourceService
import newsref.model.Api
import newsref.model.core.NewsSpan
import newsref.server.utilities.getById
import newsref.server.utilities.getByPath
import newsref.server.utilities.readFromCall

fun Routing.articleRouting(
    service: ArticleDtoService = ArticleDtoService()
) {

//    getByPath(Api.TopSources) {
//        val span = it.spanOrdinal.readFromCall(call).let { ordinal ->
//            NewsSpan.entries.firstOrNull { it.ordinal == ordinal }
//        } ?: NewsSpan.WEEK
//        val sources = service.getTopSources(span.duration, 20)
//        call.respond(sources)
//    }

    getById(Api.sourceEndpoint) { id, endpoint ->
        val source = service.readArticle(id)
        if (source == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(source)
        }
    }
}