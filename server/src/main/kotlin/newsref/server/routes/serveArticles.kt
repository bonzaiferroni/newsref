package newsref.server.routes

import io.ktor.server.routing.*
import newsref.db.services.ArticleDtoService
import newsref.model.Api
import newsref.server.utilities.getIdEndpoint

fun Routing.serveArticles(
    service: ArticleDtoService = ArticleDtoService()
) {

//    getEndpoint(Api.TopSources) {
//        val span = it.spanOrdinal.readFromCall(call).let { ordinal ->
//            NewsSpan.entries.firstOrNull { it.ordinal == ordinal }
//        } ?: NewsSpan.WEEK
//        val sources = service.getTopSources(span.duration, 20)
//        call.respond(sources)
//    }

    getIdEndpoint(Api.GetArticleById) { id, endpoint ->
        service.readArticle(id)
    }
}