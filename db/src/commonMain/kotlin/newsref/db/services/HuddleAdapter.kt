package newsref.db.services

import newsref.db.model.*
import newsref.model.core.*
import newsref.model.data.*
import kotlin.time.Duration

sealed class HuddleAdapter(
    val type: HuddleType,
    val duration: Duration,
) {
    abstract suspend fun readOptions(key: HuddleKey): List<HuddleOption>
    abstract suspend fun readCurrentValue(key: HuddleKey): String
    abstract suspend fun readGuide(key: HuddleKey): String
    abstract suspend fun updateDatabase(consensus: String, huddle: Huddle)
}