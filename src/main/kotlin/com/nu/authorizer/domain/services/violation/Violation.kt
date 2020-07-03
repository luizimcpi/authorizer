package com.nu.authorizer.domain.services.violation

import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.entities.Transaction
import com.nu.authorizer.domain.model.requests.TransactionRequest

interface Violation {
    fun check(
        transactionRequest: TransactionRequest? = null,
        account: Account? = null,
        violations: MutableList<String>,
        lastTransactions: List<Transaction>? = null,
        violationDescription: String
    )
}
