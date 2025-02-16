package newsref.krawly.agents

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.*
import newsref.db.model.StoryFinderLog
import newsref.db.model.StoryFinderState
import newsref.db.services.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("ChapterLinker")

class ChapterLinker(
    private val dataLogService: DataLogService = DataLogService(),
    private val storyComposerService: ChapterComposerService = ChapterComposerService()
) : StateModule<StoryFinderState>(StoryFinderState()) {

    fun start() {
        coroutineScope.launch {
            val savedState = dataLogService.read(StoryFinderLog.state)
            if (savedState != null)
                setState { savedState }
            while (true) {
                delay(1.minutes)
                dataLogService.write(StoryFinderLog.state, stateNow)
            }
        }
        coroutineScope.launch {
            while (true) {
                linkChapters()
                delay(60.seconds)
            }
        }
    }

    private suspend fun linkChapters() {
        val chapters = storyComposerService.readParentIsNull()
        if (chapters.isEmpty()) {
            console.log("no orphans")
            setState { it.copy(noOrphans = it.noOrphans + 1)}
            return
        }

        for (chapter in chapters) {
            val signal = storyComposerService.findNearestParent(chapter)
            if (signal == null) {
                // console.log("no signal")
                setState { it.copy(noSignal = it.noSignal + 1)}
                continue
            }
            val (distance, parentChapter) = signal
            if (distance > STORY_MAX_DISTANCE) {
                // console.log("no signal in range")
                setState { it.copy(noSignalInRange = it.noSignalInRange)}
                continue
            }

            storyComposerService.setParent(chapter.id, parentChapter.id)

//            val storyId = if (parentChapter.storyId == null) {
//                if (chapter.storyId != null) {
//                    // console.log("extended story back")
//                    setState { it.copy(extendedBack = it.extendedBack + 1) }
//                    chapter.storyId
//                } else if (parentChapter.size > MIN_STORY_SIZE) {
//                    console.log("created story")
//                    setState { it.copy(storyCreated = it.storyCreated + 1) }
//                    storyComposerService.createStory(parentChapter.happenedAt)
//                } else {
//                    // console.log("story below min size")
//                    setState { it.copy(storyBelowMin = it.storyBelowMin + 1) }
//                    null
//                }
//            } else {
//                // console.log("extended story forward")
//                setState { it.copy(extendedForward = it.extendedForward + 1) }
//                parentChapter.storyId
//            }
//
//            if (storyId == null) continue
//
//            storyComposerService.setStory(chapter.id, storyId)
//            storyComposerService.setStory(parentChapter.id, storyId)
//            storyComposerService.setHappenedAt(storyId, parentChapter.happenedAt)
//            storyComposerService.updateSize(storyId)
//            val previousStoryId = chapter.storyId
//            if (previousStoryId != null && previousStoryId != storyId)
//                storyComposerService.updateSize(previousStoryId)
        }
    }
}