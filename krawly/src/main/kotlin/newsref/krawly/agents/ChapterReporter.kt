package newsref.krawly.agents

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.globalConsole
import newsref.db.console.Background
import newsref.db.console.Justify
import newsref.db.console.LogHandle
import newsref.db.console.midnightGreenBg
import newsref.db.core.DataLogger
import newsref.db.models.ChapterFinderLog
import newsref.db.models.ChapterFinderState
import newsref.db.services.DataLogService
import newsref.model.utils.agoFormat
import kotlin.time.Duration.Companion.minutes

data class ConsoleReporter(
    val chapterFinder: ChapterFinderState = ChapterFinderState(),
    val reportedTime: Instant = Instant.DISTANT_PAST
)

private val console = globalConsole.getHandle("Reporter")

class ChapterReporter(
    private val dataLogService: DataLogService = DataLogService(),
): CrawlerModule() {

    fun start() {
        coroutineScope.launch {
            var previousState = dataLogService.read(ChapterFinderLog.state) ?: ChapterFinderState()
            while(true) {
                delay(1.minutes)
                val newState = dataLogService.read(ChapterFinderLog.state) ?: continue
                reportChapterFinder(newState, previousState)
                previousState = newState
            }
        }
    }

    private fun reportChapterFinder(newState: ChapterFinderState, previousState: ChapterFinderState) {
        console.deltaTable(
            newState, previousState, midnightGreenBg,
            IntColumn<ChapterFinderState>("Excluded") { it.exclusions },
            IntColumn<ChapterFinderState>("Empty") { it.emptySignals },
            IntColumn<ChapterFinderState>("Primary") { it.primarySignals },
            IntColumn<ChapterFinderState>("Secondary") { it.secondarySignals },
            IntColumn<ChapterFinderState>("Missing") { it.contentsMissing },
            IntColumn<ChapterFinderState>("Vectors") { it.vectorsFetched },
            IntColumn<ChapterFinderState>("Buckets") { it.buckets },
            IntColumn<ChapterFinderState>("Chapters") { it.chapters },
            IntColumn<ChapterFinderState>("New") { it.chaptersCreated },
            IntColumn<ChapterFinderState>("Updated") { it.chaptersUpdated },
            IntColumn<ChapterFinderState>("Deleted") { it.chaptersDeleted },
            TimeColumn<ChapterFinderState>("Progress") { it.signalDate },
        )
    }
}

fun <T> LogHandle.deltaTable(new: T, old: T, background: Background, vararg columns: DeltaColumn<T>) {
    for(column in columns) {
        this.cell(column.name, column.getWidth(new, old), justify = Justify.LEFT)
    }
    this.send(background = background)
    for(column in columns) {
        this.cell(column.getNew(new), column.getWidth(new, old), justify = Justify.RIGHT)
    }
    this.send(background = background)
    for(column in columns) {
        this.cell(column.getDelta(new, old), column.getWidth(new, old), justify = Justify.RIGHT)
    }
    this.send(background = background)
}

sealed class DeltaColumn<T> {
    abstract val name: String

    fun getNew(new: T) = when (this) {
        is IntColumn<T> -> this.getter(new).toString()
        is TimeColumn<T> -> this.getter(new).agoFormat()
    }

    fun getDelta(new: T, old: T) = when (this) {
        is IntColumn<T> -> (this.getter(new) - this.getter(old)).let { "${if (it > 0) "+" else ""}$it" }
        is TimeColumn<T> -> (this.getter(new) - this.getter(old)).agoFormat()
    }

    fun getWidth(new: T, old: T) = maxOf(this.getNew(new).length, this.getDelta(new, old).length, 3)
}

data class IntColumn<T>(
    override val name: String,
    val getter: (T) -> Int,
) : DeltaColumn<T>()

data class TimeColumn<T>(
    override val name: String,
    val getter: (T) -> Instant
) : DeltaColumn<T>()