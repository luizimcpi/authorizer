package com.nu.authorizer.resources.repositories

import com.nu.authorizer.domain.model.entities.Transaction
import com.nu.authorizer.domain.repositories.TransactionRepository
import java.util.concurrent.atomic.AtomicInteger

class TransactionInMemoryRepository : TransactionRepository {

    private var transactions = mutableMapOf<Int, Transaction>()
    private var lastId: AtomicInteger = AtomicInteger(transactions.size - 1)

    override fun save(transaction: Transaction) {
        val id = lastId.incrementAndGet()
        transactions[id] = transaction
    }
}
