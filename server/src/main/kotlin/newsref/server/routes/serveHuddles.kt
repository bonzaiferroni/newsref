package newsref.server.routes

import io.ktor.server.routing.*
import klutch.db.services.UserApiService
import klutch.server.getById
import klutch.server.post
import newsref.db.services.*
import newsref.model.Api
import klutch.utils.getClaim
import klutch.server.CLAIM_USERNAME
import klutch.server.authenticateJwt

fun Routing.serveHuddles(
    service: HuddleService = HuddleService(),
    userService: UserApiService = UserApiService()
) {
    getById(Api.Huddles.GetHuddleContentById) { id, endpoint ->
        service.readHuddleContent(id)
    }

    getById(Api.Huddles.GetHuddleResponsesById) { id, endpoint ->
        service.readHuddleResponses(id)
    }

    post(Api.Huddles.ReadHuddlePrompt) { sent, endpoint ->
        service.readPrompt(sent)
    }

    authenticateJwt {
        getById(Api.Huddles.GetUserResponseId) { id, endpoint ->
            val username = call.getClaim(CLAIM_USERNAME)
            val userId = userService.readIdByUsername(username)
            service.readUserResponseId(id, userId)
        }

        post(Api.Huddles.SubmitHuddleResponse) { sent, endpoint ->
            val username = call.getClaim(CLAIM_USERNAME)
            val userId = userService.readIdByUsername(username)
            service.createHuddleResponse(sent, userId)
        }
    }
}