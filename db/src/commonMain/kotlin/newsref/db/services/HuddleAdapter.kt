package newsref.db.services

import newsref.db.model.*
import newsref.model.data.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

sealed class HuddleAdapter(
    val type: HuddleType,
    val duration: Duration = 60.minutes,
) {
    abstract suspend fun readOptions(key: HuddleKey): List<HuddleOption>
    abstract suspend fun readCurrentValue(key: HuddleKey): String?
    abstract suspend fun readGuide(key: HuddleKey): String
    abstract suspend fun updateDatabase(consensus: String, huddle: Huddle)
}