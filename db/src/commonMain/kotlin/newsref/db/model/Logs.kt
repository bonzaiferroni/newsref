package newsref.db.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable


@Serializable
data class ChapterFinderState(
    val exclusions: Int = 0,
    val primarySignals: Int = 0,
    val secondarySignals: Int = 0,
    val emptySignals: Int = 0,
    val contentsMissing: Int = 0,
    val vectorsFetched: Int = 0,
    val buckets: Int = 0,
    val chapters: Int = 0,
    val chaptersCreated: Int = 0,
    val chaptersUpdated: Int = 0,
    val chaptersDeleted: Int = 0,
    val signalDate: Instant = Instant.DISTANT_FUTURE,
)

object ChapterFinderLog: LogSet("ChapterFinder") {
    val state = JsonLog<ChapterFinderState>("state", this)
}

@Serializable
data class StoryFinderState(
    val noOrphans: Int = 0,
    val noSignal: Int = 0,
    val noSignalInRange: Int = 0,
    val extendedBack: Int = 0,
    val extendedForward: Int = 0,
    val storyCreated: Int = 0,
    val storyBelowMin: Int = 0,
)

object StoryFinderLog: LogSet("StoryFinder") {
    val state = JsonLog<StoryFinderState>("state", this)
}