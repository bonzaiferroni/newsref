package newsref.model.dto

data class ChapterPackDto(
    val chapter: ChapterDto,
    val sources: List<SourceDto>
)