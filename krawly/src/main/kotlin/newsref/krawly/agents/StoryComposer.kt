package newsref.krawly.agents

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.globalConsole
import newsref.db.services.*
import newsref.db.utils.averageAndNormalize
import newsref.db.utils.distance
import kotlin.time.Duration.Companion.seconds

private val console = globalConsole.getHandle("ChapterLinker")

class StoryComposer(
    private val dataLogService: DataLogService = DataLogService(),
    private val storyComposerService: StoryComposerService = StoryComposerService()
): CrawlerModule() {
    fun start() {
        coroutineScope.launch {
            while (true) {
                composeStories()
                delay(60.seconds)
            }
        }
    }

    private suspend fun composeStories() {
        val chapters = storyComposerService.readNullStoryDistances()
        for ((chapter, vector) in chapters) {
            val storyId = chapter.storyId
            if (storyId != null) {
                removeChapterFromStory(storyId, chapter.id)
            }

            val pair = storyComposerService.readNearestStoryDistance(vector, storyId)
            if (pair == null || pair.second > STORY_MAX_DISTANCE) {
                console.log("created story")
                storyComposerService.createStory(chapter, vector)
            } else {
                val (story, distance) = pair
                val pairs = storyComposerService.readStoryChapterVectors(story.id) + (chapter to vector)
                console.log("updating story size: ${pairs.size}, distance: $distance, ${story.id}")
                val averageVector = averageAndNormalize(pairs.map { it.second })
                val chapterDistances = pairs.map { (chapter, vector) -> chapter to distance(averageVector, vector) }
                storyComposerService.updateStory(story, averageVector, chapterDistances)
            }
        }
    }

    private suspend fun removeChapterFromStory(storyId: Long, chapterId: Long) {
        val story = storyComposerService.readStoryById(storyId) ?: error("story not found: $storyId")
        val pairs = storyComposerService.readStoryChapterVectors(storyId)
            .filter { (chapter, vector) -> chapter.id != chapterId }
        if (pairs.isEmpty()) {
            storyComposerService.deleteStory(storyId)
        } else {
            val averageVector = pairs
                .map { (id, vector) -> vector }
                .let { averageAndNormalize(it) }
            val distances = pairs.map { (id, vector) -> id to distance(vector, averageVector) }
            storyComposerService.updateStory(story, averageVector, distances)
        }
    }
}