package newsref.app.io

import newsref.model.Api
import newsref.model.data.HuddleSeed

class HuddleStore : ApiStore() {
    suspend fun createHuddle(seed: HuddleSeed): Long? = client.post(Api.Huddles, seed)
}