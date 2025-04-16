package newsref.krawly.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.globalConsole
import newsref.db.services.StoryComposerService
import kotlin.time.Duration.Companion.minutes

private val console = globalConsole.getHandle("StoryWatcher")

class StoryWatcher(
    private val storyComposerService: StoryComposerService = StoryComposerService()
) {

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            console.logTrace("watching chapters")
            while (true) {
                // watchStories()
                delay(1.minutes)
            }
        }
    }

    private suspend fun watchStories() {
        val stories = storyComposerService.readAllBelowSizeOrNull()
        console.log("stories to size: ${stories.size}")
        for (story in stories) {
            val chapters = storyComposerService.readStoryChapters(story.id)
            if (chapters.isEmpty()) {
                storyComposerService.deleteStory(story.id)
                continue
            }
            val size = chapters.size
            val score = chapters.sumOf { it.score }
            storyComposerService.updateSize(story.id, size, score)
        }
    }
}