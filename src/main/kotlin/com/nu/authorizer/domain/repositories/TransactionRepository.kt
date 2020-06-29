package com.nu.authorizer.domain.repositories

import com.nu.authorizer.domain.model.entities.Transaction

interface TransactionRepository {
    fun save(transaction: Transaction)
    fun getLastTransactions(): List<Transaction>
}
