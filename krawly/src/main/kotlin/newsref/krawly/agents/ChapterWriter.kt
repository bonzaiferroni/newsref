package newsref.krawly.agents

import newsref.db.model.Narrator
import java.io.File

class ChapterWriter {
}

private val defaultNarrator = Narrator(
    name = "Chambers",
    bio = File("../docs/chambers.bio").readText(),
    chatModelUrl = "https://api-inference.huggingface.co/models/deepseek-ai/DeepSeek-R1-Distill-Qwen-32B",
    // chatToken = environment["HF_TOKEN"]
)

private val initialTaskWithPrimary = File("../docs/narrator-initial-task-with-primary.txt")

// val originContent = contentService.readPageContentText(origin.id)
//        val centralContent = contentService.readPageContentText(centralSource.id)
//        val initialTask = buildString {
//            appendLine(narrator.bio)
//            if (originContent.isNotEmpty()) {
//                appendLine(initialTaskWithPrimary)
//                appendLine("Here is the primary source:")
//                appendLine(originContent)
//            } else {
//
//            }
//            appendLine("Here is the news article:")
//            appendLine(centralContent)
//        }