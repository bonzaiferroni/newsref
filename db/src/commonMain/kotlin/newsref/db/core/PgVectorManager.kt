package newsref.db.core

import com.pgvector.PGvector
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

internal class PgVectorManager(private val impl: TransactionManager) : TransactionManager by impl {
	override fun newTransaction(isolation: Int, readOnly: Boolean, outerTransaction: Transaction?): Transaction {
		val transaction = impl.newTransaction(isolation, readOnly, outerTransaction)
		val conn = transaction.connection.connection as Connection
		PGvector.addVectorType(conn)
		return transaction
	}
}