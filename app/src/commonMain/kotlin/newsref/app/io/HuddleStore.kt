package newsref.app.io

import newsref.model.Api
import newsref.model.data.HuddleKey
import newsref.model.data.HuddlePrompt
import newsref.model.data.HuddleResponseSeed
import pondui.io.ApiStore

class HuddleStore : ApiStore() {
    suspend fun createHuddle(seed: HuddleResponseSeed) = client.post(Api.Huddles.SubmitHuddleResponse, seed)
    suspend fun readPrompt(key: HuddleKey): HuddlePrompt = client.post(Api.Huddles.ReadHuddlePrompt, key)
    suspend fun readHuddleContent(huddleId: Long) = client.get(Api.Huddles.GetHuddleContentById, huddleId)
    suspend fun readHuddleResponses(huddleId: Long) = client.get(Api.Huddles.GetHuddleResponsesById, huddleId)
    suspend fun readUserResponseId(huddleId: Long) = client.getOrNull(Api.Huddles.GetUserResponseId, huddleId)
}