package streetlight.app.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import streetlight.app.PantryDb
import kotlinx.coroutines.Dispatchers

class ServingDao(db: PantryDb) {
    private val queries = db.servingQueries

    fun insert(foodId: Long, grams: Double) {
        queries.insert(
            food_id = foodId,
            grams = grams,
        )
    }

    fun update(id: Long, foodId: Long, grams: Double) {
        queries.update(
            id = id,
            foodId = foodId,
            grams = grams,
        )
    }

    fun delete(id: Long) {
        queries.delete(id = id)
    }

    fun getAll() = queries.getAll().asFlow().mapToList(Dispatchers.IO)
}