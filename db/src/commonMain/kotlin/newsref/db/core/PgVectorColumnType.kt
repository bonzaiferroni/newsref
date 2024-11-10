package newsref.db.core

import com.pgvector.PGvector
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject
import java.sql.ResultSet

// referenced:
// https://github.com/Martmists-GH/mlutils/blob/master/src/main/kotlin/com/martmists/mlutils/compat/exposed/PgVectorColumnType.kt

class PgVectorColumnType(private val size: Int) : ColumnType<Embedding>() {
	override fun sqlType(): String = "vector($size)"

	override fun readObject(rs: ResultSet, index: Int) = rs.getObject(index) as PGvector?

	override fun validateValueBeforeUpdate(value: Embedding?) {
		if (value !is Embedding) error("Value must be an Embedding")
		require(value.vector.size == size) { "Embedding size must be $size" }
	}

	override fun valueFromDB(value: Any) = when (value) {
		is PGvector -> Embedding(value.toArray())
		else -> error("Unexpected value: $value")
	}

	override fun notNullValueToDB(value: Embedding) = PGvector(value.vector)
}