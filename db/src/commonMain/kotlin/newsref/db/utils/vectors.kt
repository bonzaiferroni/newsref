package newsref.db.utils

fun normalize(vector: FloatArray): FloatArray {
    val magnitude = kotlin.math.sqrt(vector.sumOf { (it * it).toDouble() })
    require(magnitude > 0) { "Cannot normalize a zero vector!" }
    return vector.map { (it / magnitude).toFloat() }.toFloatArray()
}

fun averageAndNormalize(vec1: FloatArray, vec2: FloatArray): FloatArray {
    require(vec1.size == vec2.size) { "Vectors must be the same size" }
    val averaged = FloatArray(vec1.size) { i -> (vec1[i] + vec2[i]) / 2 }
    return normalize(averaged)
}

fun weightedAverageAndNormalize(
    vec1: FloatArray,
    vec2: FloatArray,
    weight1: Int,
    weight2: Int
): FloatArray {
    require(vec1.size == vec2.size) { "Vectors must be the same size" }
    require(weight1 >= 0 && weight2 >= 0) { "Weights must be non-negative" }

    // Compute weighted average
    val weightedAverage = FloatArray(vec1.size) { i ->
        (vec1[i] * weight1 + vec2[i] * weight2) / (weight1 + weight2)
    }

    // Normalize the result
    val magnitude = kotlin.math.sqrt(weightedAverage.sumOf { (it * it).toDouble() })
    require(magnitude > 0) { "Cannot normalize a zero vector!" }
    return weightedAverage.map { (it / magnitude).toFloat() }.toFloatArray()
}

fun cosineDistance(vec1: FloatArray, vec2: FloatArray): Float {
    require(vec1.size == vec2.size) { "Vectors must be the same size" }

    // Calculate dot product
    val dotProduct = vec1.zip(vec2).sumOf { (a, b) -> (a * b).toDouble() }

    // Calculate magnitudes
    val magnitude1 = kotlin.math.sqrt(vec1.sumOf { (it * it).toDouble() })
    val magnitude2 = kotlin.math.sqrt(vec2.sumOf { (it * it).toDouble() })

    // Check for zero magnitudes to avoid division by zero
    require(magnitude1 > 0 && magnitude2 > 0) { "Cannot calculate cosine distance for zero vectors!" }

    return 1 - (dotProduct / (magnitude1 * magnitude2)).toFloat()
}

fun dotProduct(vec1: FloatArray, vec2: FloatArray): Float {
    require(vec1.size == vec2.size) { "Vectors must be the same size" }
    return vec1.zip(vec2).sumOf { (a, b) -> (a * b).toDouble() }.toFloat()
}

fun distance(vec1: FloatArray, vec2: FloatArray) = 1 - dotProduct(vec1, vec2)

fun averageAndNormalize(vectors: List<FloatArray>): FloatArray {
    require(vectors.isNotEmpty()) { "The list of vectors cannot be empty" }
    val size = vectors.first().size
    require(vectors.all { it.size == size }) { "All vectors must be the same size" }

    // Calculate the element-wise average
    val averaged = FloatArray(size) { i ->
        vectors.sumOf { it[i].toDouble() }.toFloat() / vectors.size
    }

    // Normalize the averaged vector
    val magnitude = kotlin.math.sqrt(averaged.sumOf { (it * it).toDouble() }).toFloat()
    require(magnitude > 0) { "Cannot normalize a zero vector!" }
    return averaged.map { it / magnitude }.toFloatArray()
}
