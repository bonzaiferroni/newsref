package newsref.app.io

import newsref.model.Api
import newsref.model.data.HuddleKey
import newsref.model.data.HuddlePrompt
import newsref.model.data.HuddleSeed

class HuddleStore : ApiStore() {
    suspend fun createHuddle(seed: HuddleSeed): Long? = client.post(Api.Huddles.CreateHuddle, seed)
    suspend fun readPrompt(key: HuddleKey): HuddlePrompt = client.post(Api.Huddles.ReadHuddlePrompt, key)
}