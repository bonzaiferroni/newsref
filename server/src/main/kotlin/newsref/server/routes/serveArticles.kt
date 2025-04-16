package newsref.server.routes

import io.ktor.server.routing.*
import newsref.db.services.PageDtoService
import newsref.model.Api
import klutch.server.getById

fun Routing.serveArticles(
    service: PageDtoService = PageDtoService()
) {

//    get(Api.TopSources) {
//        val span = it.spanOrdinal.readFromCall(call).let { ordinal ->
//            NewsSpan.entries.firstOrNull { it.ordinal == ordinal }
//        } ?: NewsSpan.WEEK
//        val sources = service.getTopSources(span.duration, 20)
//        call.respond(sources)
//    }

    getById(Api.Pages.GetArticleById) { id, endpoint ->
        service.readPage(id)
    }
}