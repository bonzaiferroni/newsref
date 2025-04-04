package newsref.model.data

enum class DataSort {
    Id,
    Time,
    Name,
    Score,
}

enum class SortDirection {
    Ascending,
    Descending
}

typealias Sorting = Pair<DataSort?, SortDirection?>

fun <T, R : Comparable<R>> List<T>.sortedByDirection(direction: SortDirection?, selector: (T) -> R) = when (direction) {
    SortDirection.Ascending, null -> this.sortedBy(selector)
    SortDirection.Descending -> this.sortedByDescending(selector)
}

fun <T, R : Comparable<R>> select(block: (T) -> R) = block