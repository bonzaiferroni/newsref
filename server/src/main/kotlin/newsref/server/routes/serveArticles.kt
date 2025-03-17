package newsref.server.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import newsref.db.services.ArticleDtoService
import newsref.model.Api
import newsref.server.utilities.getById

fun Routing.serveArticles(
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