package newsref.app.ui

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import io.ktor.websocket.Frame.Text
import pondui.ui.controls.PropertyTile
import pondui.utils.format
import newsref.model.data.Chapter
import newsref.model.data.HuddleType
import newsref.model.data.HuddleKey
import newsref.model.utils.formatSpanLong

@Composable
fun ChapterPropertyRow(
    chapter: Chapter
) {
    PropertyRow {
        PropertyTile("Id", chapter.id)
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