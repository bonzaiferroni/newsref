package newsref.dashboard.ui.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import newsref.dashboard.basePadding
import newsref.dashboard.roundedCorners
import newsref.model.utils.toInstantFromEpoch
import kotlin.math.log10
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun TimeChart(
    config: TimeChartData,
    type: ChartType = ChartType.Line
) {
    if (config.times.isEmpty()) {
        Text("No data")
        return
    }

    val tz = TimeZone.currentSystemDefault()

    val (seriesList, timeStart, intervalCount, interval, yAxisMax) = remember(config) { generateChartInfo(config) }

    Box(
        modifier = Modifier.height(400.dp)
            .clip(roundedCorners)
            .background(MaterialTheme.colorScheme.surfaceDim)
            .padding(basePadding)
    ) {
        FloatChart(
            seriesList = seriesList,
            type = type,
            yAxisMax = yAxisMax,
            xAxisMax = intervalCount.toFloat(),
            xAxisLabels = {
                val dateTime = (timeStart + (interval * it.toDouble())).toLocalDateTime(tz)
                if (dateTime.hour == 0 && dateTime.minute == 0) {
                    dateTime.dayOfWeek.toString().take(3)
                } else {
                    "${dateTime.hour}:${dateTime.minute.toString().padStart(2, '0')}"
                }
            }
        )
    }
}

private fun generateChartInfo(
    data: TimeChartData
): TimeChartInfo {
    val (times, seriesList) = data
    val tz = TimeZone.currentSystemDefault()
    val timeStart = times.first().toLocalDateTime(tz).date
        .atStartOfDayIn(tz)
    val timeEnd = (times.last() + 1.days).toLocalDateTime(tz).date
        .atStartOfDayIn(tz)
    val totalInterval = timeEnd - timeStart
    val interval = when {
        totalInterval > 2.days -> 1.days
        else -> 6.hours
    }
    val pointLists = mutableListOf<Pair<Float, FloatPointSeries>>()
    val timeFloats = times.map { ((it - timeStart) / interval).toFloat() }
    var maxValue = 0f
    for ((name, values) in seriesList) {
        var sum = 0f
        val pointList = values.mapIndexed { index, value ->
            sum += value
            maxValue = maxOf(maxValue, value)
            FloatPoint(timeFloats[index], value)
        }.toImmutableList()
        pointLists.add(sum to FloatPointSeries(name, pointList))
    }

    return TimeChartInfo(
        seriesList = pointLists.sortedByDescending { it.first }
            .map { it.second }
            .toImmutableList(),
        timeStart = timeStart,
        intervalCount = (totalInterval / interval).toInt(),
        interval = interval,
        yAxisMax = maxValue
    )
}

fun <T> Collection<T>.toTimeChartData(
    getTime: (T) -> Instant,
    buckets: Int?,
    vararg configList: TimeSeriesConfig<T>
): TimeChartData? {
    if (this.isEmpty()) return null
    val bucketSize = this.size / (buckets ?: this.size)
    val sorted = this.sortedBy { getTime(it) }

    // generate time arrays
    val times = mutableListOf<Instant>()
    var timeSum = 0L
    var timeIndex = 0
    var timesInBucket = 0
    for (item in sorted) {
        timeSum += getTime(item).epochSeconds
        if (++timesInBucket == bucketSize || ++timeIndex == this.size) {
            times.add((timeSum / timesInBucket).toInstantFromEpoch())
            timeSum = 0
            timesInBucket = 0
        }
    }

    // generate value arrays
    val seriesList = mutableListOf<FloatSeries>()
    for ((name, fromDelta, getter) in configList) {
        val values = mutableListOf<Float>()
        var previous: T? = null
        var itemsInBucket = 0
        var sum = 0f
        var index = 0
        for (item in sorted) {
            sum += getter(item, previous)
            if (++itemsInBucket == bucketSize || ++index == this.size) {
                val value = if (fromDelta) sum else sum / itemsInBucket
                values.add(value)
                sum = 0f
                itemsInBucket = 0
            }
            previous = item
        }
        seriesList.add(FloatSeries(name, values.toImmutableList()))
    }
    return TimeChartData(times.toImmutableList(), seriesList.toImmutableList())
}

fun <T> getItemDelta(getter: (T) -> Float) =
    { item: T, previous: T? -> previous?.let { getter(item) - getter(it) } ?: 0f }

fun <T> getItemValue(getter: (T) -> Float) = { item: T, previous: T? -> getter(item) }

fun nextOrderOfMagnitude(value: Float): Float = 10f.pow(log10(value).toInt() + 1)

fun <T> createTimeSeries(name: String, fromDelta: Boolean = false, getter: (T) -> Float) = TimeSeriesConfig(
    name = name,
    fromDelta = fromDelta,
    getter = if (fromDelta) getItemDelta(getter) else getItemValue(getter)
)

data class TimeChartData(
    val times: ImmutableList<Instant>,
    val vectors: ImmutableList<FloatSeries>,
)

data class TimeSeriesConfig<T>(
    val name: String,
    val fromDelta: Boolean,
    val getter: (T, T?) -> Float,
)

private data class TimeChartInfo(
    val seriesList: ImmutableList<FloatPointSeries>,
    val timeStart: Instant,
    val intervalCount: Int,
    val interval: Duration,
    val yAxisMax: Float,
)