package newsref.krawly.utils

import kotlin.math.sqrt

fun dbscan(
    values: List<DbscanValue>,
    epsilon: Double,
    minPoints: Int
): Map<Int, List<DbscanValue>> {
    val visited = mutableSetOf<DbscanValue>()
    val clusters = mutableMapOf<Int, MutableList<DbscanValue>>()
    var clusterId = 0

    for (value in values) {
        if (value in visited) continue
        visited.add(value)

        val neighbors = findNeighbors(value, values, epsilon)
        if (neighbors.size < minPoints) continue // Mark as noise if not enough neighbors

        clusterId++
        clusters[clusterId] = mutableListOf()
        expandCluster(value, neighbors, clusters[clusterId]!!, visited, values, epsilon, minPoints)
    }

    return clusters
}

fun findNeighbors(
    point: DbscanValue,
    values: List<DbscanValue>,
    epsilon: Double
): List<DbscanValue> {
    return values.filter { distance(it.value, point.value) <= epsilon }
}

fun expandCluster(
    point: DbscanValue,
    neighbors: List<DbscanValue>,
    cluster: MutableList<DbscanValue>,
    visited: MutableSet<DbscanValue>,
    values: List<DbscanValue>,
    epsilon: Double,
    minPoints: Int
) {
    cluster.add(point)

    val queue = neighbors.toMutableList()
    while (queue.isNotEmpty()) {
        val neighbor = queue.removeAt(0)
        if (neighbor !in visited) {
            visited.add(neighbor)
            val neighborNeighbors = findNeighbors(neighbor, values, epsilon)
            if (neighborNeighbors.size >= minPoints) queue.addAll(neighborNeighbors)
        }

        if (neighbor !in cluster) cluster.add(neighbor)
    }
}

fun distance(a: Double, b: Double): Double = kotlin.math.sqrt((a - b) * (a - b))

data class DbscanValue(val id: Long, val value: Double)
