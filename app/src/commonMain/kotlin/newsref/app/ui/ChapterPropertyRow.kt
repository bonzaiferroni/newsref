package newsref.app.ui

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import newsref.app.blip.controls.PropertyTile
import newsref.app.blip.controls.Text
import newsref.model.data.Chapter
import newsref.app.utils.format
import newsref.model.data.HuddleType
import newsref.model.data.HuddleKey
import newsref.model.utils.formatSpanLong

@Composable
fun ChapterPropertyRow(
    chapter: Chapter
) {
    PropertyRow {
        PropertyTile("Title", chapter.title) {
            SelectionContainer {
                Text(it)
            }
            HuddleEditorControl(
                huddleName = "Edit Chapter Title",
                key = HuddleKey(
                    chapterId = chapter.id,
                    type = HuddleType.EditChapterTitle
                )
            )
        }
        PropertyTile("Score", chapter.score)
        PropertyTile("Size", chapter.size)
        PropertyTile("Published (average)", chapter.averageAt.formatSpanLong())
        PropertyTile("Cohesion", chapter.cohesion.format())
    }
}